package com.task4.task4.viewmodels

import androidx.lifecycle.Transformations
import com.task4.task4.database.Task

class TaskListViewModel : BaseViewModel() {

    val taskListLiveData = Transformations.map(taskRepository.getTasksWithSubtasks()) {
        it.map { task -> taskRepository.getTaskWithSubtasks(task.parent.id) }
    }

    fun addTask(task: Task) {
        taskRepository.createTask(task)
    }

    fun deleteTask(task : Task){
        taskRepository.deleteTask(task)
    }
}