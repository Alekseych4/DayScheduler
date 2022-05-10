package com.open.day.dayscheduler.controller.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.open.day.dayscheduler.DayScheduleFragmentDirections
import com.open.day.dayscheduler.R
import com.open.day.dayscheduler.model.TaskModel
import java.util.*

class DayScheduleAdapter(private val values: List<TaskModel>, private val navController: NavController)
    : RecyclerView.Adapter<DayScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_schedule_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDataWithView(values[position])
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.task_title_text_view)
        val timeStart: TextView = itemView.findViewById(R.id.time_start_text_view)
        val timeEnd: TextView = itemView.findViewById(R.id.time_end_text_view)
        val chipGroup: ChipGroup = itemView.findViewById(R.id.schedule_item_chip_group)

        fun bindDataWithView(taskModel: TaskModel) {
            title.text = taskModel.title
            timeStart.text = getTime(taskModel.startTime)
            timeEnd.text = getTime(taskModel.endTime)
            taskModel.tags.forEach { chipGroup.addView(getChip(it)) }

            itemView.setOnClickListener(onListItemClickListener(taskModel.id))
        }

        private fun getTime(millis: Long): String {
            return DateFormat.format("kk:mm", millis) as String
        }

        private fun getChip(tag: String): Chip {
            val chip = Chip(chipGroup.context)
            chip.text = tag
            chip.height = ChipGroup.LayoutParams.WRAP_CONTENT
            chip.width = ChipGroup.LayoutParams.WRAP_CONTENT
            return chip
        }

        private fun onListItemClickListener(id: UUID): View.OnClickListener {
            return View.OnClickListener {
                val action = DayScheduleFragmentDirections.actionDayScheduleFragmentToTaskCreationFragment(id)
                navController.navigate(action)
            }
        }
    }
}