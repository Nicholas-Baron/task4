package com.task4.task4.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.task4.task4.database.TaskRepository
import com.task4.task4.database.TaskWithSubTasks

open class BaseViewModel : ViewModel() {

    protected val taskRepository = TaskRepository.get()

    fun saveTask(task: TaskWithSubTasks) {
        taskRepository.update(task)
    }

    protected class DoubleTrigger<A, B>(a: LiveData<A>, b: LiveData<B>) :
        MediatorLiveData<Pair<A?, B?>>() {

        init {
            addSource(a) { value = it to b.value }
            addSource(b) { value = a.value to it }
        }
    }

    protected class TripleTrigger<A, B, C>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>) :
        MediatorLiveData<Triple<A?, B?, C?>>() {

        init {
            addSource(a) { value = Triple(it, b.value, c.value) }
            addSource(b) { value = Triple(a.value, it, c.value) }
            addSource(c) { value = Triple(a.value, b.value, it) }
        }
    }
}