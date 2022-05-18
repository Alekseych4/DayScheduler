package com.open.day.dayscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.open.day.dayscheduler.R
import com.open.day.dayscheduler.controller.adapter.DayScheduleAdapter
import com.open.day.dayscheduler.databinding.FragmentScheduleItemBinding
import com.open.day.dayscheduler.databinding.FragmentScheduleItemsListBinding
import com.open.day.dayscheduler.databinding.TaskCreationFragmentBinding
import com.open.day.dayscheduler.viewModel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

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

        taskViewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.submitList(tasks)
        })

        return binding.root
    }

    private fun onFabClickListener(): View.OnClickListener {
        return View.OnClickListener {
            this.findNavController().navigate(DayScheduleFragmentDirections.actionDayScheduleFragmentToTaskCreationFragment())
        }
    }
}