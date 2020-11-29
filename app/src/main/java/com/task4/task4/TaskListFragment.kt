package com.task4.task4

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task4.task4.database.Task
import com.task4.task4.database.TaskWithSubTasks

private const val TAG = "TaskListFragment"

class TaskListFragment : Fragment() {

    // the list of tasks presented to the user
    private lateinit var taskRecyclerView: RecyclerView

    // adapts the list of tasks  from the database for the recycler view
    private var adapter = TaskAdapter(emptyList())

    // preserve data across rotations lazy load it in
    private val taskListViewModel: TaskListViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter.bind(layoutInflater, mutableListOf(context as TaskRecylerViewCallbacks))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        taskRecyclerView = view.findViewById(R.id.task_recycler_view)
        taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskListViewModel.taskListLiveData.observe(viewLifecycleOwner) { tasks ->
            tasks?.let {
                Log.i(TAG, "Got tasks ${tasks.size}")
                updateUI(tasks)
            }
        }
    }

    // remove the reference to the callback receiver
    override fun onDetach() {
        super.onDetach()
        adapter.clearCallbacks()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.new_task -> {
            val task = Task()
            taskListViewModel.addTask(task)
            adapter.triggerCallbacks(task.id)
            true
        }
        else          -> super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        taskListViewModel.saveTasks(adapter.tasks)
    }

    private fun updateUI(tasks: List<TaskWithSubTasks>) {
        adapter.tasks = tasks
        taskRecyclerView.adapter = adapter
    }

    companion object {

        fun newInstance() = TaskListFragment()
    }
}
