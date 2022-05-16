package com.open.day.dayscheduler.controller.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.open.day.dayscheduler.R
import com.open.day.dayscheduler.exception.NoSuchRowException
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.ui.DayScheduleFragmentDirections
import java.util.UUID
import com.open.day.dayscheduler.util.TimeCountingUtils.Companion.millisToHoursAndMinutes as getTime

class DayScheduleAdapter(private val navController: NavController)
    : ListAdapter<TaskModel, DayScheduleAdapter.TasksViewHolder>(tasksComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_schedule_item, parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.bindDataWithView(getItem(position))
    }

    inner class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.task_title_text_view)
        val timeStart: TextView = itemView.findViewById(R.id.time_start_text_view)
        val timeEnd: TextView = itemView.findViewById(R.id.time_end_text_view)
        val chipGroup: ChipGroup = itemView.findViewById(R.id.schedule_item_chip_group)

        fun bindDataWithView(taskModel: TaskModel) {
            if (taskModel.id == null)
                throw NoSuchRowException("Item doesn't have an id.")
            if (!taskModel.isReminder && taskModel.endTime != null)
                timeEnd.text = getTime(taskModel.endTime as Long)

            title.text = taskModel.title
            timeStart.text = getTime(taskModel.startTime)
            chipGroup.addView(getChip(Resources.getSystem().getString(taskModel.tag.tagName)))

            itemView.setOnClickListener(onListItemClickListener(taskModel.id))
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

    companion object {
        private val tasksComparator = object : DiffUtil.ItemCallback<TaskModel>() {
            override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }
}