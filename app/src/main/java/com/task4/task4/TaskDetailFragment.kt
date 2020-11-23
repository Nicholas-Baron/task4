package com.task4.task4

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.task4.task4.database.Task
import java.util.UUID

private const val ARG_TASK_ID = "task_id"

class TaskDetailFragment : Fragment() {

    private lateinit var task: Task
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var taskCompleted: CheckBox
    private val taskDetailViewModel: TaskDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = Task()
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
            ) {/*bazinga*/
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                task.name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {/*bazoonkle*/
            }
        })

        taskCompleted.apply {
            setOnCheckedChangeListener { _, isChecked ->
                task.completed = isChecked
            }
        }

        dateButton.setOnClickListener {
            //TODO: get date from user
        }

        timeButton.setOnClickListener {
            //TODO: get date from user
        }
    }

    override fun onStop() {
        super.onStop()
        taskDetailViewModel.saveTask(task)
    }

    //TODO: correctly set due date and due time in UI
    private fun updateUI() {
        titleField.setText(task.name)
        dateButton.text = task.dueDate.toString()
        taskCompleted.apply {
            isChecked = task.completed
            jumpDrawablesToCurrentState()
        }
        timeButton.text = task.dueDate.toString()
    }

    companion object {
        fun newInstance(taskId : UUID) = TaskDetailFragment().apply {
            arguments = bundleOf(ARG_TASK_ID to taskId)
        }
    }


}