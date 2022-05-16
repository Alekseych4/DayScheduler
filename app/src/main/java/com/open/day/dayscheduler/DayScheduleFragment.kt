package com.open.day.dayscheduler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.open.day.dayscheduler.controller.adapter.DayScheduleAdapter
import com.open.day.dayscheduler.viewModel.TasksViewModel

/**
 * A fragment representing a list of Items.
 */
class DayScheduleFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_items_list, container, false)
        val scheduleRecyclerView: RecyclerView = view.findViewById(R.id.schedule_items_list)
        val taskViewModel: TasksViewModel by viewModels()

        val adapter = DayScheduleAdapter(this.findNavController())

        scheduleRecyclerView.layoutManager = LinearLayoutManager(context)
        scheduleRecyclerView.adapter = adapter

        taskViewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.submitList(tasks)
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                DayScheduleFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}