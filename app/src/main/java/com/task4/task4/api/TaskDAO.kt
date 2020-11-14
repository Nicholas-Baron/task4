package com.task4.task4.api

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.UUID

@Dao
interface TaskDAO {

    @Transaction
    @Query("SELECT * FROM Task")
    fun getTasksWithSubtasks(): LiveData<List<TaskWithSubTasks>>

    @Query("SELECT * FROM Task WHERE id=(:id)")
    fun getTask(id: UUID): LiveData<Task?>

    @Update
    fun updateTask(task: Task)

    @Insert
    fun createTask(task: Task)
}