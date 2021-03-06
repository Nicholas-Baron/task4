package com.task4.task4.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Task::class, TaskCrossRef::class], version = 1)
@TypeConverters(TaskTypeConverters::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDAO(): TaskDAO
}