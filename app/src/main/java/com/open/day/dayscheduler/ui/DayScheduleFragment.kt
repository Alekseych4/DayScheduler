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
import com.open.day.dayscheduler.viewModel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class DayScheduleFragment : Fragment() {

    private val taskViewModel: TasksViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_items_list, container, false)
        val scheduleRecyclerView: RecyclerView = view.findViewById(R.id.schedule_items_list)

        val adapter = DayScheduleAdapter(this.findNavController())

        scheduleRecyclerView.layoutManager = LinearLayoutManager(context)
        scheduleRecyclerView.adapter = adapter

        taskViewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.submitList(tasks)
        })

        return view
    }
}