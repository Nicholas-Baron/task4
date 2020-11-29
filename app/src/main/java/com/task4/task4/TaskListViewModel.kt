package com.task4.task4

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.task4.task4.database.Task
import com.task4.task4.database.TaskRepository
import com.task4.task4.database.TaskWithSubTasks

class TaskListViewModel : ViewModel() {

    private val taskRepository = TaskRepository.get()
    val taskListLiveData = Transformations.map(taskRepository.getTasksWithSubtasks()) {
        it.map { taskRepository.getTaskWithSubtasks(it.parent.id) }
    }

    fun addTask(task: Task) {
        taskRepository.createTask(task)
    }

    fun saveTasks(tasks: List<TaskWithSubTasks>) {
        tasks.forEach { saveTask(it) }
    }

    fun saveTask(task: TaskWithSubTasks) {
        taskRepository.update(task)
    }
}