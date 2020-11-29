package com.task4.task4.viewmodels

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.task4.task4.database.Task
import com.task4.task4.database.TaskRepository
import com.task4.task4.database.TaskWithSubTasks

class TaskListViewModel : BaseViewModel() {

    val taskListLiveData = Transformations.map(taskRepository.getTasksWithSubtasks()) {
        it.map { taskRepository.getTaskWithSubtasks(it.parent.id) }
    }

    fun addTask(task: Task) {
        taskRepository.createTask(task)
    }
}