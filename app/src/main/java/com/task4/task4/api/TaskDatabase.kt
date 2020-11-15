package com.task4.task4.api

import androidx.room.Database
import androidx.room.TypeConverters

@Database(entities = [Task::class, TaskCrossRef::class], version = 1)
@TypeConverters(TaskTypeConverters::class)
abstract class TaskDatabase {

    abstract fun taskDAO(): TaskDAO
}