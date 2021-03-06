@file:Suppress("unused", "unused")

package com.task4.task4.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "task-database"

class TaskRepository private constructor(context: Context) {

    private val database: TaskDatabase = Room.databaseBuilder(
        context.applicationContext, TaskDatabase::class.java, DATABASE_NAME
    ).build()

    private val taskDAO: TaskDAO = database.taskDAO()

    private val executor = Executors.newSingleThreadExecutor()

    // CRUD time

    fun createTask(task: Task) {
        executor.execute { taskDAO.createTask(task) }
    }

    fun deleteTask(task: Task) {
        executor.execute {
            taskDAO.deleteTask(task)
            taskDAO.deleteCrossRefs(task.id)
        }
    }

    fun getTasksWithSubtasks(): LiveData<List<TaskWithSubTasks>> = taskDAO.getTasksWithSubtasks()

    fun getTasks(): LiveData<List<Task>> = taskDAO.getTasks()

    fun getTaskWithSubtasks(uuid: UUID): LiveData<TaskWithSubTasks?> =
        taskDAO.getTaskWithSubtasks(uuid)

    fun getTask(uuid: UUID): LiveData<Task?> = taskDAO.getTask(uuid)

    fun getTaskCrossRefs(): LiveData<List<TaskCrossRef>> = taskDAO.getCrossRefs()

    fun update(task: Task) {
        executor.execute { taskDAO.updateTask(task) }
    }

    fun update(task: TaskWithSubTasks) {
        executor.execute {
            taskDAO.updateTask(task.parent)
            task.subTasks.forEach { taskDAO.updateTask(it) }
        }
    }

    fun linkTasks(parent: UUID, subtask: UUID) {
        executor.execute {
            taskDAO.insertCrossRef(
                TaskCrossRef(parent, subtask)
            )
        }
    }

    companion object {

        private var INSTANCE: TaskRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = TaskRepository(context)
        }

        fun get(): TaskRepository =
            INSTANCE ?: throw IllegalStateException("Task repository not initialized")
    }
}