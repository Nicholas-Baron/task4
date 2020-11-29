package com.task4.task4.viewmodels

import androidx.lifecycle.*
import com.task4.task4.database.Task
import com.task4.task4.database.TaskRepository
import com.task4.task4.database.TaskWithSubTasks
import java.util.UUID

class TaskDetailViewModel : BaseViewModel() {

    private val taskIdLiveData = MutableLiveData<UUID>()
    val taskLiveData: LiveData<TaskWithSubTasks?> =
        Transformations.switchMap(taskIdLiveData) { taskId ->
            taskRepository.getTaskWithSubtasks(taskId)
        }

    private val subTaskLiveData = taskRepository.getTaskCrossRefs()

    val liveSubTasksWithSubTasks: LiveData<List<LiveData<TaskWithSubTasks?>>> =
        Transformations.map(DoubleTrigger(taskLiveData, subTaskLiveData)) {
            val directSubTasks = it.first?.subTasks ?: return@map emptyList()
            val allTasksWithSubTasks = it.second ?: return@map emptyList()

            val subTaskIds = directSubTasks.map { it.id }.toSet()

            return@map allTasksWithSubTasks.filter { it.parentId in subTaskIds }.map { crossRef ->
                taskRepository.getTaskWithSubtasks(crossRef.childId)
            }
        }

    fun loadTask(taskId: UUID) {
        taskIdLiveData.value = taskId
    }
}