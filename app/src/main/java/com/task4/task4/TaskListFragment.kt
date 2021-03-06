package com.task4.task4

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task4.task4.database.Task
import com.task4.task4.database.TaskWithSubTasks
import com.task4.task4.viewmodels.TaskListViewModel

private const val TAG = "TaskListFragment"

class TaskListFragment : Fragment() {

    // the list of tasks presented to the user
    private lateinit var taskRecyclerView: RecyclerView

    // preserve data across rotations lazy load it in
    private val taskListViewModel: TaskListViewModel by viewModels()

    // adapts the list of tasks  from the database for the recycler view
    private var adapter =
        TaskAdapter(mutableListOf(), TaskRecyclerViewSettings { taskListViewModel.saveTask(it) })

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter.bind(layoutInflater, mutableListOf(context as TaskRecyclerViewCallbacks))
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
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = taskRecyclerView.adapter as TaskAdapter
                taskListViewModel.deleteTaskAt(viewHolder.adapterPosition)
                adapter.removeAt(viewHolder.adapterPosition)

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(taskRecyclerView)
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

    private fun updateUI(tasks: List<LiveData<TaskWithSubTasks?>>) {
        adapter.tasks = tasks.toMutableList()
        taskRecyclerView.adapter = adapter
    }

    companion object {

        fun newInstance() = TaskListFragment()
    }
}
