package com.task4.task4

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task4.task4.database.Task
import com.task4.task4.database.TaskWithSubTasks
import java.util.UUID

private const val ARG_PARENT_ID = "parent_id"

class AddSubtaskFragment : Fragment(), TaskRecylerViewCallbacks {


    private lateinit var parentTask: Task

    // GUI elements
    private lateinit var createNewSubTask: Button
    private lateinit var existingTaskRecyclerView: RecyclerView

    private val addSubtaskViewModel: AddSubtaskViewModel by viewModels()

    private var existingTaskAdapter =
        TaskAdapter(emptyList(), TaskRecylerViewSettings(backMotion = true, showCheckBox = false))

    private var possibleChildrenTasks: List<TaskWithSubTasks> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        existingTaskAdapter.bind(
            layoutInflater, callbacks = mutableListOf(
                context as TaskRecylerViewCallbacks, this
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parentTaskId = arguments?.getSerializable(ARG_PARENT_ID) as UUID
        addSubtaskViewModel.loadParentTask(parentTaskId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_subtask, container, false).apply {
        createNewSubTask = findViewById(R.id.create_new_subtask)
        existingTaskRecyclerView = findViewById(R.id.existing_tasks_list)
        existingTaskRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = existingTaskAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSubtaskViewModel.parentTaskLiveData.observe(viewLifecycleOwner) {
            if (it != null) parentTask = it
        }
        addSubtaskViewModel.possibleChildrenLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                possibleChildrenTasks = it
                updateUI()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        createNewSubTask.setOnClickListener {
            val task = Task()
            addSubtaskViewModel.addSubtask(parentTask, subtask = task)
            existingTaskAdapter.triggerCallbacks(task.id, backMotion = false)
        }
    }

    private fun updateUI() {
        existingTaskAdapter.tasks = possibleChildrenTasks
        existingTaskRecyclerView.adapter = existingTaskAdapter
    }

    companion object {

        fun newInstance(parentTaskId: UUID) = AddSubtaskFragment().apply {
            arguments = bundleOf(ARG_PARENT_ID to parentTaskId)
        }
    }

    override fun onTaskSelected(taskId: UUID, moveBack: Boolean) {
        if (moveBack) addSubtaskViewModel.linkTasks(parent = parentTask.id, subtask = taskId)
    }
}