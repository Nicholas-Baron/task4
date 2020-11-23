package com.task4.task4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.UUID

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //TODO: override this function with task list callbacks
    fun onTaskSelected(taskId: UUID) {
        val fragment = TaskDetailFragment.newInstance(taskId)
        TODO("Blocked by pull request on taskView")
    }
}