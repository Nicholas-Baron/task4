package com.task4.task4

import androidx.lifecycle.*
import com.task4.task4.database.Task
import com.task4.task4.database.TaskRepository
import com.task4.task4.database.TaskWithSubTasks
import java.util.UUID

class TaskDetailViewModel : ViewModel() {


    private val taskRepository = TaskRepository.get()
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

    fun saveTask(task: TaskWithSubTasks) {
        taskRepository.update(task.parent)
        for (subTask in task.subTasks) {
            taskRepository.update(subTask)
            taskRepository.linkTasks(task.parent.id, subTask.id)
        }
    }

    fun addSubtask(parent: Task, subtask: Task) {
        taskRepository.createTask(subtask)
        taskRepository.linkTasks(parent.id, subtask.id)
    }

    private class DoubleTrigger<A, B>(a: LiveData<A>, b: LiveData<B>) :
        MediatorLiveData<Pair<A?, B?>>() {

        init {
            addSource(a) { value = it to b.value }
            addSource(b) { value = a.value to it }
        }
    }
}