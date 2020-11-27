package com.task4.task4

import android.app.Application
import com.task4.task4.database.TaskRepository

@Suppress("unused")
class Task4Application : Application() {

    override fun onCreate() {
        super.onCreate()
        TaskRepository.initialize(this)
    }
}