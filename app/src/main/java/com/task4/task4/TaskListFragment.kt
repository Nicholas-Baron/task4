package com.task4.task4

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task4.task4.database.Task
import java.util.UUID

private const val TAG = "TaskListFragment"

class TaskListFragment : Fragment() {

    // the list of tasks presented to the user
    private lateinit var taskRecyclerView: RecyclerView

    // adapts the list of tasks  from the database for the recycler view
    private var adapter: TaskAdapter? = TaskAdapter(emptyList())

    // preserve data across rotations lazy load it in
    private val taskListViewModel: TaskListViewModel by viewModels()

    private inner class TaskHolder(view: View) : RecyclerView.ViewHolder(view),
                                                 View.OnClickListener {

        private lateinit var task: Task
        private val titleTextView: TextView = itemView.findViewById(R.id.task_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.task_due_date)
        private val doneCheckBox: CheckBox = itemView.findViewById(R.id.task_completed)

        fun bind(task: Task) {
            this.task = task
            this.task.apply {
                titleTextView.text = name
                dateTextView.text = userDate
                doneCheckBox.apply {
                    isChecked = completed
                    jumpDrawablesToCurrentState()
                }
            }
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            callbacks?.onTaskSelected(task.id)
        }
    }

    interface Callbacks {

        fun onTaskSelected(taskId: UUID)
    }

    private var callbacks: Callbacks? = null

    private inner class TaskAdapter(var tasks: List<Task>) : RecyclerView.Adapter<TaskHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder = TaskHolder(
            layoutInflater.inflate(R.layout.list_item_task, parent, false)
        )

        override fun getItemCount() = tasks.size

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            holder.bind(tasks[position])
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        taskRecyclerView = view.findViewById(R.id.task_recycler_view)

        taskRecyclerView.layoutManager = LinearLayoutManager(context)
        taskRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskListViewModel.taskListLiveData.observe(viewLifecycleOwner, { tasks ->
            tasks?.let {
                Log.i(TAG, "Got tasks ${tasks.size}")
                updateUI(tasks)
            }

        })
    }

    // remove the reference to the callback receiver
    override fun onDetach() {
        super.onDetach()
        callbacks = null
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
            callbacks?.onTaskSelected(task.id)
            true
        }
        else          -> super.onOptionsItemSelected(item)
    }


    private fun updateUI(tasks: List<Task>) {
        adapter = TaskAdapter(tasks)
        taskRecyclerView.adapter = adapter
    }

    companion object {

        fun newInstance() = TaskListFragment()
    }
}
