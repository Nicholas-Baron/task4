package com.task4.task4.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.UUID

@Dao
interface TaskDAO {

    @Transaction
    @Query("SELECT * FROM Task")
    fun getTasksWithSubtasks(): LiveData<List<TaskWithSubTasks>>

    @Query("SELECT * FROM Task")
    fun getTasks(): LiveData<List<Task>>

    @Transaction
    @Query("SELECT * FROM Task WHERE id=(:id)")
    fun getTaskWithSubtasks(id: UUID): LiveData<TaskWithSubTasks?>

    @Query("SELECT * FROM Task WHERE id=(:id)")
    fun getTask(id: UUID): LiveData<Task?>

    @Update
    fun updateTask(task: Task)

    @Insert
    fun createTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCrossRef(crossRef: TaskCrossRef)

    @Query("SELECT * FROM TaskCrossRef")
    fun getCrossRefs(): LiveData<List<TaskCrossRef>>

    @Query("DELETE FROM TaskCrossRef WHERE parentId = (:id) OR childId = (:id)")
    fun deleteCrossRefs(id : UUID)
}