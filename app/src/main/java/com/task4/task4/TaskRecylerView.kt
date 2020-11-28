package com.task4.task4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.task4.task4.database.Task
import java.util.UUID

class TaskAdapter(
    var tasks: List<Task>, var callbacks: TaskRecylerViewCallbacks? = null
) : RecyclerView.Adapter<TaskHolder>() {

    // The layoutInflater is `lateinit` as it must be assigned in `onAttach`.
    lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder = TaskHolder(
        layoutInflater.inflate(R.layout.list_item_task, parent, false), callbacks
    )

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.bind(tasks[position])
    }

}

interface TaskRecylerViewCallbacks {

    fun onTaskSelected(taskId: UUID)
}

class TaskHolder(view: View, val callbacks: TaskRecylerViewCallbacks?) :
    RecyclerView.ViewHolder(view), View.OnClickListener {

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