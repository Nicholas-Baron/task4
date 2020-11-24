package com.task4.task4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.task4.task4.database.Task
import com.task4.task4.database.TaskRepository
import java.util.UUID

class TaskDetailViewModel : ViewModel(){

    private val taskRepository = TaskRepository.get()
    private val taskIdLiveData = MutableLiveData<UUID>()
    var taskLiveData : LiveData<Task?> = Transformations.switchMap(taskIdLiveData) {
        taskId -> taskRepository.getTask(taskId)
    }

    fun loadTask(taskId : UUID){
        taskIdLiveData.value = taskId
    }
    fun saveTask(task: Task){
        taskRepository.update(task)
    }

}