package com.task4.task4.viewmodels

import androidx.lifecycle.*
import com.task4.task4.database.Task
import com.task4.task4.database.TaskCrossRef
import com.task4.task4.database.TaskWithSubTasks
import java.util.UUID

class AddSubtaskViewModel : BaseViewModel() {

    private val parentTaskIdLiveData = MutableLiveData<UUID>()
    val parentTaskLiveData: LiveData<Task?> =
        Transformations.switchMap(parentTaskIdLiveData) { taskId ->
            taskRepository.getTask(taskId)
        }

    private val crossRefLiveData: LiveData<List<TaskCrossRef>> = taskRepository.getTaskCrossRefs()

    private val allTasks: LiveData<List<TaskWithSubTasks>> = taskRepository.getTasksWithSubtasks()

    val possibleChildrenLiveData: LiveData<List<LiveData<TaskWithSubTasks?>>> =
        Transformations.map(TripleTrigger(parentTaskIdLiveData, crossRefLiveData, allTasks)) {
            val crossRefs = it.second ?: return@map emptyList()
            val parentTaskId = it.first ?: return@map emptyList()
            val allTasks = it.third ?: return@map emptyList()

            // The issue is that any task in direct lineage
            // (yourself, parents, grandparents, great-grandparents) cannot be children.
            // However, any cousins will just be pulled down

            val lineage = mutableSetOf(parentTaskId)
            val toVisit = mutableListOf(parentTaskId)
            while (toVisit.isNotEmpty()) {
                val visiting = toVisit.removeAt(0)
                val parents =
                    crossRefs.filter { ref -> ref.childId == visiting }.map { ref -> ref.parentId }
                        .toSet()

                lineage.addAll(parents)
                toVisit.addAll(parents)
            }

            return@map allTasks.filter { task -> task.parent.id !in lineage }
                .map { task -> taskRepository.getTaskWithSubtasks(task.parent.id) }
        }

    fun loadParentTask(taskId: UUID) {
        parentTaskIdLiveData.value = taskId
    }

    fun addSubtask(parent: Task, subtask: Task) {
        taskRepository.createTask(subtask)
        linkTasks(parent = parent.id, subtask = subtask.id)
    }

    fun linkTasks(parent: UUID, subtask: UUID) {
        taskRepository.linkTasks(parent, subtask)
    }

}