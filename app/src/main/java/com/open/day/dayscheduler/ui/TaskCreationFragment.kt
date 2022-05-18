package com.open.day.dayscheduler.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.open.day.dayscheduler.R
import com.open.day.dayscheduler.databinding.TaskCreationFragmentBinding
import com.open.day.dayscheduler.model.Tag
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.util.TimeCountingUtils.Companion.utcMillisToLocalDayDateMonth
import com.open.day.dayscheduler.util.TimeCountingUtils.Companion.utcMillisToLocalHoursAndMinutes
import com.open.day.dayscheduler.viewModel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.UUID

@AndroidEntryPoint
class TaskCreationFragment : Fragment() {

    private val viewModel: TasksViewModel by activityViewModels()
    private var _binding: TaskCreationFragmentBinding? = null
    private val binding get() = _binding!!
    private val args: TaskCreationFragmentArgs by navArgs()
    private var taskId: UUID? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = TaskCreationFragmentBinding.inflate(inflater, container, false)

        Tag.values().forEach {
            val chip = Chip(this.context)
            chip.text = resources.getText(it.stringResId)
            chip.height = ChipGroup.LayoutParams.WRAP_CONTENT
            chip.width = ChipGroup.LayoutParams.WRAP_CONTENT
            chip.isCheckable = true
            binding.newTaskChipGroup.addView(chip)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskId = args.id

        if (taskId == null) {
            val calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
            viewModel.date.value = calendar.timeInMillis
            viewModel.startTime.value = calendar.timeInMillis
            //FIXME: possible bug when startTime.value == 23:59
            calendar.add(Calendar.MINUTE, 1)
            viewModel.endTime.value = calendar.timeInMillis
        } else {
            viewModel.updateTaskById(taskId as UUID)
        }

        fabTransition(binding.bottomAppBar)

        val navController = findNavController()

        binding.addSaveNewTaskFab.setOnClickListener(onAddNewTaskClickListener(navController))
        binding.reminderCheckbox.setOnClickListener(onReminderCheckboxClickListener())
        binding.anchorCheckbox.setOnClickListener(onAnchorCheckboxClickListener())
        binding.newTaskDescriptionEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.newTaskDescriptionInputLayout.error = null
                viewModel.setTitle(binding.newTaskDescriptionEditText.text.toString())
            }
        }
        binding.newTaskTitleEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.newTaskTitleLayout.error = null
                viewModel.setTitle(binding.newTaskTitleEditText.text.toString())
            }
        }
        binding.newTaskChipGroup.setOnCheckedChangeListener(onChipChecked())
        binding.newTaskDateLayout.setEndIconOnClickListener(onDateClickListener())
        binding.newTaskTimeStartLayout.setEndIconOnClickListener(onSetTimePicker(R.id.new_task_time_start))
        binding.newTaskTimeEndLayout.setEndIconOnClickListener(onSetTimePicker(R.id.new_task_time_end))

        viewModel.title.observe(viewLifecycleOwner, Observer { setTitle(it) })
        viewModel.startTime.observe(viewLifecycleOwner, Observer { setStartTime(it) })
        viewModel.date.observe(viewLifecycleOwner, Observer { setDate(it) })
        viewModel.endTime.observe(viewLifecycleOwner, Observer { setEndTime(it) })
        viewModel.isAnchor.observe(viewLifecycleOwner, Observer { setAnchorCheckBox(it) })
        viewModel.isReminder.observe(viewLifecycleOwner, Observer { setReminderCheckBox(it) })
        viewModel.description.observe(viewLifecycleOwner, Observer { setDescription(it) })
        viewModel.tag.observe(viewLifecycleOwner, Observer {
            if (it != null) setTag(resources.getString(it.stringResId))
        })

        // Errors
        viewModel.titleInputError.observe(viewLifecycleOwner, Observer {
            binding.newTaskTitleLayout.error = resources.getString(it)
        })
        viewModel.startError.observe(viewLifecycleOwner, Observer {
            binding.newTaskTimeStartLayout.error = resources.getString(it)
        })
        viewModel.endError.observe(viewLifecycleOwner, Observer {
            binding.newTaskTimeEndLayout.error = resources.getString(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onAddNewTaskClickListener(navController: NavController): View.OnClickListener {
        return View.OnClickListener {
            viewModel.setTitle(binding.newTaskTitleEditText.text.toString())
            viewModel.setIsAnchor(binding.anchorCheckbox.isChecked)
            viewModel.setIsReminder(binding.reminderCheckbox.isChecked)
            viewModel.setDescription(binding.newTaskDescriptionEditText.text.toString())
            viewModel.validateTime()

            if (!hasError()) {
                viewModel.saveCurrentTask()
                navController.navigate(TaskCreationFragmentDirections.actionTaskCreationFragmentToDayScheduleFragment())
            }
        }
    }

    private fun afterTitleTextChanged(title: String) {
        binding.newTaskTitleLayout.error = null
        viewModel.setTitle(title)
    }

    private fun afterDescriptionTextChanged(description: String) {
        viewModel.description.value = description
    }

    private fun onReminderCheckboxClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val reminderBox = it as MaterialCheckBox

            if (reminderBox.isChecked) {
                binding.anchorCheckbox.isClickable = false
                binding.newTaskTimeEndLayout.visibility = View.INVISIBLE
            } else {
                binding.anchorCheckbox.isClickable = true
                binding.newTaskTimeEndLayout.visibility = View.VISIBLE
            }
            viewModel.isReminder.value = reminderBox.isChecked
        }
    }

    private fun onAnchorCheckboxClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val anchorBox = it as MaterialCheckBox
            binding.reminderCheckbox.isClickable = !anchorBox.isChecked
            viewModel.isAnchor.value = anchorBox.isChecked
        }
    }

    private fun onChipChecked(): ChipGroup.OnCheckedChangeListener {
        return ChipGroup.OnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val tag = Tag.values().find {
                resources.getString(it.stringResId) == chip.text
            }
            viewModel.tag.value = tag
        }
    }

    private fun onDateClickListener(): View.OnClickListener {
        return View.OnClickListener { view ->
            val calendar = Calendar.getInstance()
            viewModel.date.value?.let { calendar.timeInMillis = it }

            val constraints = CalendarConstraints.Builder()
                .setStart(calendar.timeInMillis)
                .setValidator(DateValidatorPointForward.now())
                .build()

            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(view.resources.getString(R.string.date_dialog_title))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints)
                .build()

            picker.addOnPositiveButtonClickListener {
//                val selectedUtc = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
//                selectedUtc.timeInMillis = it
//                val selectedLocal = Calendar.getInstance()
//                selectedLocal.clear()
//                selectedLocal.set(selectedUtc.get(Calendar.YEAR), selectedUtc.get(Calendar.MONTH), selectedUtc.get(Calendar.DATE))

                viewModel.date.value = it
            }

            picker.show(this.childFragmentManager, "datePicker")
        }
    }

    private fun onSetTimePicker(viewId: Int): View.OnClickListener {
        return View.OnClickListener { view ->
            val is24HourFormat = DateFormat.is24HourFormat(this.context)
            val clockFormat = if (is24HourFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            val localCalendar = Calendar.getInstance()

            if (viewId == R.id.new_task_time_start) {
                binding.newTaskTimeStartLayout.error = null
                binding.newTaskTimeEndLayout.error = null

                viewModel.startTime.value?.let {
                    localCalendar.timeInMillis = it
                }

                val picker = MaterialTimePicker.Builder()
                    .setTitleText(view.resources.getString(R.string.start_time_dialog_title))
                    .setTimeFormat(clockFormat)
                    .setHour(localCalendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(localCalendar.get(Calendar.MINUTE))
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .build()

                picker.addOnPositiveButtonClickListener {
                    viewModel.setStartHourMinutes(picker.hour, picker.minute,
                        localCalendar.get(Calendar.HOUR_OF_DAY), localCalendar.get(Calendar.MINUTE))
                }
                picker.show(this.childFragmentManager, "startTimePicker")

            } else if (viewId == R.id.new_task_time_end) {
                binding.newTaskTimeEndLayout.error = null
                binding.newTaskTimeStartLayout.error = null

                viewModel.endTime.value?.let {
                    localCalendar.timeInMillis = it
                }

                val picker = MaterialTimePicker.Builder()
                    .setTitleText(view.resources.getString(R.string.end_time_dialog_title))
                    .setTimeFormat(clockFormat)
                    .setHour(localCalendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(localCalendar.get(Calendar.MINUTE))
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .build()

                picker.addOnPositiveButtonClickListener {
                    viewModel.setEndHourMinutes(picker.hour, picker.minute,
                        localCalendar.get(Calendar.HOUR_OF_DAY), localCalendar.get(Calendar.MINUTE))
                }

                picker.show(this.childFragmentManager, "startTimePicker")
            }
        }
    }

    private fun setFields(task: TaskModel?) {
        if (task != null) {
            setTitle(task.title)
            setDate(task.startTime)
            setStartTime(task.startTime)
            setEndTime(task.endTime)
            setReminderCheckBox(task.isReminder)
            setAnchorCheckBox(task.isAnchor)
            setDescription(task.description)
        }
    }

    private fun setTitle(title: String) {
        binding.newTaskTitleLayout.error = null
        binding.newTaskTitleEditText.setText(title)
    }

    private fun setDate(millis: Long) = binding.newTaskDate.setText(utcMillisToLocalDayDateMonth(millis))

    private fun setStartTime(millis: Long) = binding.newTaskTimeStart.setText(utcMillisToLocalHoursAndMinutes(millis))

    private fun setEndTime(millis: Long?) {
        if (millis == null) {
            binding.newTaskTimeEndLayout.visibility = View.INVISIBLE
        } else {
            binding.newTaskTimeEndLayout.visibility = View.VISIBLE
            binding.newTaskTimeEnd.setText(utcMillisToLocalHoursAndMinutes(millis))
        }
    }

    private fun setReminderCheckBox(isReminder: Boolean) {
        if (isReminder) {
            binding.anchorCheckbox.isClickable = false
            binding.reminderCheckbox.isClickable = true
            binding.reminderCheckbox.isChecked = isReminder
        }
    }

    private fun setAnchorCheckBox(isAnchor: Boolean) {
        if (isAnchor) {
            binding.reminderCheckbox.isClickable = false
            binding.anchorCheckbox.isClickable = true
            binding.anchorCheckbox.isChecked = isAnchor
        }
    }

    private fun setDescription(description: String?) = binding.newTaskDescriptionEditText.setText(description)

    private fun fabTransition(appBar: BottomAppBar) {
        appBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        appBar.fabCradleMargin = 0.0F
        appBar.fabCradleRoundedCornerRadius = 0.0F
        binding.addSaveNewTaskFab.setImageResource(R.drawable.ic_save_task_24)
    }

    private fun setTag(tagText: String?) {
        if (tagText != null) {
            binding.newTaskChipGroup.forEach {
                val chip = it as Chip
                if (chip.text == tagText)
                    chip.isChecked = true
            }
        }
    }

    private fun hasError(): Boolean {
        return binding.newTaskDateLayout.error != null || binding.newTaskTimeStartLayout.error != null ||
                binding.newTaskTitleLayout.error != null || binding.newTaskTimeEndLayout.error != null ||
                binding.newTaskDescriptionInputLayout.error != null
    }
}