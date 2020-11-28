package com.task4.task4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.task4.task4.database.Task
import com.task4.task4.database.TaskRepository
import com.task4.task4.database.TaskWithSubTasks
import java.util.UUID

class TaskDetailViewModel : ViewModel() {

    private val taskRepository = TaskRepository.get()
    private val taskIdLiveData = MutableLiveData<UUID>()
    var taskLiveData: LiveData<TaskWithSubTasks?> =
        Transformations.switchMap(taskIdLiveData) { taskId ->
            taskRepository.getTaskWithSubtasks(taskId)
        }

    fun loadTask(taskId: UUID) {
        taskIdLiveData.value = taskId
    }

    fun saveTask(task: TaskWithSubTasks) {
        taskRepository.update(task.parent)
        for (subTask in task.subTasks) taskRepository.update(subTask)
    }

    fun addSubtask(parent: Task, subtask: Task) {
        taskRepository.createTask(subtask)
        taskRepository.linkTasks(parent, subtask)
    }
}