package com.task4.task4

import androidx.lifecycle.*
import com.task4.task4.database.Task
import com.task4.task4.database.TaskCrossRef
import com.task4.task4.database.TaskRepository
import com.task4.task4.database.TaskWithSubTasks
import java.util.UUID

class AddSubtaskViewModel : ViewModel() {

    private val taskRepository = TaskRepository.get()
    private val parentTaskIdLiveData = MutableLiveData<UUID>()
    var parentTaskLiveData: LiveData<Task?> =
        Transformations.switchMap(parentTaskIdLiveData) { taskid ->
            taskRepository.getTask(taskid)
        }

    var crossRefLiveData: LiveData<List<TaskCrossRef>> = taskRepository.getTaskCrossRefs()

    var allTasks: LiveData<List<TaskWithSubTasks>> = taskRepository.getTasksWithSubtasks()

    val possibleChildrenLiveData: LiveData<List<TaskWithSubTasks>> =
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
                    crossRefs.filter { it.childId == visiting }.map { it.parentId }.toSet()

                lineage.addAll(parents)
                toVisit.addAll(parents)
            }

            return@map allTasks.filter { it.parent.id !in lineage }
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

    private class TripleTrigger<A, B, C>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>) :
        MediatorLiveData<Triple<A?, B?, C?>>() {

        init {
            addSource(a) { value = Triple(it, b.value, c.value) }
            addSource(b) { value = Triple(a.value, it, c.value) }
            addSource(c) { value = Triple(a.value, b.value, it) }
        }
    }

}