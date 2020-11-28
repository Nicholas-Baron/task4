package com.task4.task4

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task4.task4.database.Task
import com.task4.task4.database.TaskWithSubTasks
import com.task4.task4.dialogs.DatePickerFragment
import com.task4.task4.dialogs.TimePickerFragment
import java.util.Date
import java.util.UUID

private const val ARG_TASK_ID = "task_id"

private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 1

class TaskDetailFragment : Fragment(), DatePickerFragment.Callbacks, TimePickerFragment.Callbacks {

    private lateinit var task: TaskWithSubTasks
    private val taskDetailViewModel: TaskDetailViewModel by viewModels()

    // GUI elements
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var taskCompleted: CheckBox
    private lateinit var addSubtaskButton: ImageButton
    private lateinit var subtaskRecyclerView: RecyclerView

    private var subTaskAdapter = TaskAdapter(emptyList(), null)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        subTaskAdapter.layoutInflater = layoutInflater
        subTaskAdapter.callbacks = context as TaskRecylerViewCallbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = TaskWithSubTasks(parent = Task(), subTasks = emptyList())
        val taskId = arguments?.getSerializable(ARG_TASK_ID) as UUID
        taskDetailViewModel.loadTask(taskId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_detail, container, false)
        view.apply {
            titleField = findViewById(R.id.task_title)
            dateButton = findViewById(R.id.task_due_date)
            timeButton = findViewById(R.id.task_due_time)
            taskCompleted = findViewById(R.id.task_completed)
            addSubtaskButton = findViewById(R.id.add_subtask)
            subtaskRecyclerView = findViewById(R.id.task_subtask_list)
            subtaskRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = subTaskAdapter
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskDetailViewModel.taskLiveData.observe(viewLifecycleOwner) { task ->
            if (task != null) {
                this.task = task
                updateUI()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        titleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                task.parent.name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        taskCompleted.apply {
            setOnCheckedChangeListener { _, isChecked ->
                task.parent.completed = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(task.parent.dueDate).apply {
                setTargetFragment(this@TaskDetailFragment, REQUEST_DATE)
                show(this@TaskDetailFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        timeButton.setOnClickListener {
            TimePickerFragment.newInstance(task.parent.dueDate).apply {
                setTargetFragment(this@TaskDetailFragment, REQUEST_TIME)
                show(this@TaskDetailFragment.parentFragmentManager, DIALOG_TIME)
            }
        }

        addSubtaskButton.setOnClickListener {
            val task = Task()
            taskDetailViewModel.addSubtask(parent = this.task.parent, subtask = task)
            subTaskAdapter.callbacks?.onTaskSelected(task.id)
        }
    }

    override fun onStop() {
        super.onStop()
        taskDetailViewModel.saveTask(task)
    }

    private fun updateUI() {
        task.parent.apply {
            titleField.setText(name)
            dateButton.text = userDate
            timeButton.text = userTime
            taskCompleted.apply {
                isChecked = completed
                jumpDrawablesToCurrentState()
            }
        }
        subTaskAdapter.tasks = task.subTasks
        subtaskRecyclerView.adapter = subTaskAdapter
    }

    companion object {

        fun newInstance(taskId: UUID) = TaskDetailFragment().apply {
            arguments = bundleOf(ARG_TASK_ID to taskId)
        }
    }

    override fun onDateSelected(date: Date) {
        task.parent.dueDate = date
        updateUI()
    }

    override fun onTimeSelected(time: Date) {
        task.parent.dueDate = time
        updateUI()
    }


}
