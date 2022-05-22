package com.open.day.dayscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.open.day.dayscheduler.R
import com.open.day.dayscheduler.controller.adapter.DayScheduleAdapter
import com.open.day.dayscheduler.databinding.FragmentScheduleItemsListBinding
import com.open.day.dayscheduler.util.TimeCountingUtils
import com.open.day.dayscheduler.viewModel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class DayScheduleFragment : Fragment() {

    private val taskViewModel: TasksViewModel by activityViewModels()
    private var _binding: FragmentScheduleItemsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentScheduleItemsListBinding.inflate(inflater, container, false)

        val adapter = DayScheduleAdapter(this.findNavController())

        binding.scheduleItemsList.layoutManager = LinearLayoutManager(context)
        binding.scheduleItemsList.adapter = adapter

        binding.addNewTaskFab.setOnClickListener(onFabClickListener())
        binding.bottomAppBar.setOnMenuItemClickListener(onMenuItemClickListener())

        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isEmpty()) {
                binding.emptyScheduleTextView.visibility = View.VISIBLE
                binding.scheduleItemsList.visibility = View.GONE
            } else {
                binding.scheduleItemsList.visibility = View.VISIBLE
                binding.emptyScheduleTextView.visibility = View.GONE
            }
            adapter.submitList(tasks)
        }

        taskViewModel.date.observe(viewLifecycleOwner) {
            binding.dateInAppBarText.text = TimeCountingUtils.utcMillisToLocalDayDateMonth(it)
        }

        return binding.root
    }

    private fun onFabClickListener(): View.OnClickListener {
        return View.OnClickListener {
            taskViewModel.date.value?.let {
                this.findNavController().navigate(DayScheduleFragmentDirections.actionDayScheduleFragmentToTaskCreationFragment(it))
            }
        }
    }

    private fun onMenuItemClickListener(): Toolbar.OnMenuItemClickListener {
        return Toolbar.OnMenuItemClickListener {
            when(it.itemId) {
                R.id.calendar_menu_item -> {
                    val selection = Calendar.getInstance()
                    taskViewModel.date.value?.let { date ->
                        selection.timeInMillis = TimeCountingUtils.addOffsetToMillis(date)
                    }

                    val picker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(this.resources.getString(R.string.date_dialog_title))
                        .setSelection(selection.timeInMillis)
                        .build()

                    picker.addOnPositiveButtonClickListener { res ->
                        taskViewModel.setDate(res)

                    }

                    picker.show(this.childFragmentManager, "listDatePicker")

                    return@OnMenuItemClickListener true
                }

                R.id.user_profile_menu_item -> {return@OnMenuItemClickListener true}
                else -> return@OnMenuItemClickListener false
            }
        }
    }
}