package com.task4.task4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.UUID

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), TaskListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get current fragment
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if(currentFragment == null) {
            val fragment =
                TaskListFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
    override fun onTaskSelected(taskId: UUID) {
        val fragment = TaskDetailFragment.newInstance(taskId)
        TODO("Blocked by pull request on taskView")
    }
}
