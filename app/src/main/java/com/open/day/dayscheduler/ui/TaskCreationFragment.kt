package com.open.day.dayscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.open.day.dayscheduler.databinding.TaskCreationFragmentBinding
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.viewModel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.open.day.dayscheduler.util.TimeCountingUtils.Companion.millisToHoursAndMinutes
import com.open.day.dayscheduler.util.TimeCountingUtils.Companion.millisToDayDateMonth
import java.util.UUID

@AndroidEntryPoint
class TaskCreationFragment : Fragment() {

    private val viewModel: TasksViewModel by activityViewModels()
    private var _binding: TaskCreationFragmentBinding? = null
    private val binding get() = _binding!!
    private val args: TaskCreationFragmentArgs by navArgs()
    private lateinit var taskId: UUID

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = TaskCreationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskId = args.id
        if (taskId != null) {
            viewModel.updateTaskById(taskId)
            setFields(viewModel.task.value)
        } else {
            viewModel.task.value = null
        }

        viewModel.task.observe(viewLifecycleOwner, Observer { task ->
            setFields(task)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()

    }

    private fun setFields(task: TaskModel?) {
        if (task != null) {
            binding.newTaskTitleEditText.setText(task.title)
            binding.newTaskDate.text = millisToDayDateMonth(task.startTime)
            binding.newTaskTimeStart.text = millisToHoursAndMinutes(task.startTime)

            if (task.endTime == null) {
                binding.newTaskFinishString.visibility = View.INVISIBLE
                binding.newTaskTimeEnd.visibility = View.INVISIBLE
            } else {
                binding.newTaskFinishString.visibility = View.VISIBLE
                binding.newTaskTimeEnd.visibility = View.VISIBLE
                binding.newTaskTimeEnd.text = millisToHoursAndMinutes(task.endTime as Long)
            }

            if (task.isReminder) {
                binding.anchorCheckbox.isClickable = false
                binding.reminderCheckbox.isClickable = true
                binding.reminderCheckbox.isChecked = task.isReminder
            }

            if (task.isAnchor) {
                binding.reminderCheckbox.isClickable = false
                binding.anchorCheckbox.isClickable = true
                binding.anchorCheckbox.isChecked = task.isAnchor
            }

            binding.newTaskDescriptionEditText.setText(task.description)
        }
    }

}