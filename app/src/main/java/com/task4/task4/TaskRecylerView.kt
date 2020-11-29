package com.task4.task4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.task4.task4.database.TaskWithSubTasks
import java.util.UUID

data class TaskRecylerViewSettings(
    var callbacks: MutableList<TaskRecylerViewCallbacks> = mutableListOf(),
    val backMotion: Boolean = false,
    val showCheckBox: Boolean = true,
    val saveCallback: (TaskWithSubTasks) -> Unit
) {

    fun triggerCallbacks(id: UUID, customBackMotion: Boolean? = null) {
        callbacks.forEach {
            it.onTaskSelected(id, customBackMotion ?: backMotion)
        }
    }
}

class TaskAdapter(
    var tasks: List<LiveData<TaskWithSubTasks?>>, val settings: TaskRecylerViewSettings
) : RecyclerView.Adapter<TaskHolder>() {

    // The layoutInflater is `lateinit` as it must be assigned in `onAttach`.
    private lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder = TaskHolder(
        layoutInflater.inflate(R.layout.list_item_task, parent, false), settings
    )

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        tasks[position].observeForever(holder)
    }

    fun clearCallbacks() {
        settings.callbacks.clear()
    }

    fun triggerCallbacks(id: UUID, backMotion: Boolean? = null) {
        settings.triggerCallbacks(id, backMotion)
    }

    fun bind(layoutInflater: LayoutInflater, callbacks: MutableList<TaskRecylerViewCallbacks>) {
        this.layoutInflater = layoutInflater
        settings.callbacks = callbacks
    }
}

interface TaskRecylerViewCallbacks {

    fun onTaskSelected(taskId: UUID, moveBack: Boolean)
}

class TaskHolder(
    view: View, val settings: TaskRecylerViewSettings
) : RecyclerView.ViewHolder(view), View.OnClickListener, Observer<TaskWithSubTasks?> {

    private lateinit var task: TaskWithSubTasks
    private val titleTextView: TextView = itemView.findViewById(R.id.task_title)
    private val dateTextView: TextView = itemView.findViewById(R.id.task_due_date)
    private val doneCheckBox: CheckBox = itemView.findViewById(R.id.task_completed)

    override fun onChanged(task: TaskWithSubTasks?) {
        task?.let {
            this.task = task
            this.task.parent.apply {
                titleTextView.text = name
                dateTextView.text = userDate
                doneCheckBox.apply {
                    isChecked = completed
                    setOnCheckedChangeListener { _, isChecked ->
                        this@TaskHolder.task.parent.completed = isChecked
                        this@TaskHolder.settings.saveCallback(this@TaskHolder.task)
                    }
                    isVisible = settings.showCheckBox
                    jumpDrawablesToCurrentState()
                }
            }
        }
    }

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        settings.triggerCallbacks(task.parent.id)
    }
}