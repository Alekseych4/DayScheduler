package com.open.day.dayscheduler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.open.day.dayscheduler.controller.adapter.DayScheduleAdapter
import com.open.day.dayscheduler.model.TaskModel
import java.util.*

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

        scheduleRecyclerView.layoutManager = LinearLayoutManager(context)
        scheduleRecyclerView.adapter = DayScheduleAdapter(listOf(
            TaskModel(UUID.randomUUID(), "Сдать сиспрог", listOf("Учеба"), 1652169003599,
                false, 1652169553599, "Ну надо, чё"),
            TaskModel(UUID.randomUUID(), "Сдать защиту", listOf("Учеба"), 1652169182396,
                false, 1652179182396, "Ну надо, чё")), this.findNavController())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController: NavController = this.findNavController()

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