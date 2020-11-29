package com.task4.task4

import androidx.lifecycle.ViewModel
import com.task4.task4.database.Task
import com.task4.task4.database.TaskRepository

class TaskListViewModel : ViewModel() {

    private val taskRepository = TaskRepository.get()
    val taskListLiveData = taskRepository.getTasks()

    fun addTask(task: Task) {
        taskRepository.createTask(task)
    }

    fun saveTasks(tasks: List<Task>) {
        for (task in tasks) taskRepository.update(task)
    }
}