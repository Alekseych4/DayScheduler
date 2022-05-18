package com.open.day.dayscheduler.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.open.day.dayscheduler.R
import com.open.day.dayscheduler.data.repository.TaskRepository
import com.open.day.dayscheduler.model.Tag
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.util.TimeCountingUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
    ) : ViewModel() {

    private val _tasks: MutableLiveData<List<TaskModel>> = MutableLiveData()
    val tasks: LiveData<List<TaskModel>> = _tasks

    private val _task: MutableLiveData<TaskModel?> = MutableLiveData()

    val title: MutableLiveData<String> = MutableLiveData()
    val date: MutableLiveData<Long> = MutableLiveData()
    val startTime: MutableLiveData<Long> = MutableLiveData()
    val endTime: MutableLiveData<Long?> = MutableLiveData()
    val isAnchor: MutableLiveData<Boolean> = MutableLiveData()
    val isReminder: MutableLiveData<Boolean> = MutableLiveData()
    val description: MutableLiveData<String?> = MutableLiveData()
    val tag: MutableLiveData<Tag?> = MutableLiveData()

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error
    private val _startError: MutableLiveData<Int> = MutableLiveData()
    val startError: LiveData<Int> = _startError
    private val _endError: MutableLiveData<Int> = MutableLiveData()
    val endError: LiveData<Int> = _endError
    private val _titleInputError: MutableLiveData<Int> = MutableLiveData()
    val titleInputError: LiveData<Int> = _titleInputError
    private val _spinner: MutableLiveData<Boolean> = MutableLiveData()
    val spinner: LiveData<Boolean> = _spinner

    init {
        viewModelScope.launch {
            taskRepository.getTasks(TimeCountingUtils.getCurrentDayInterval())
                .collect { _tasks.value = it }
        }
    }

    fun saveCurrentTask() {
        val titleString = title.value
        val startLong = startTime.value
        val rem = isReminder.value
        val anc = isAnchor.value

        if (titleString != null && startLong != null && rem != null && anc != null) {
            val toSave = TaskModel(
                _task.value?.id,
                titleString,
                tag.value,
                startLong,
                rem,
                anc,
                endTime.value,
                description.value
            )
            try {
                saveTask(toSave)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _task.value = null
                title.value = String()
                isAnchor.value = false
                isReminder.value = false
                description.value = null
                tag.value = null
                updateLocalTasks()
            }
        } else {
            _error.value = "Data is invalid"
        }
    }

    private fun saveTask(taskModel: TaskModel) {
        viewModelScope.launch { taskRepository.saveTask(taskModel) }
    }

    fun deleteTask(id: UUID) {
        viewModelScope.launch { taskRepository.deleteTaskById(id) }
    }

    private fun updateLocalTasks() {
        launchDataLoad { taskRepository.getTasks(TimeCountingUtils.getCurrentDayInterval())
            .collect{ _tasks.value = it }
        }
    }

    private fun launchDataLoad(funToSuspend: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                funToSuspend()
            } catch (e: Exception) {
                Log.e("ERROR", e.message ?: " ")
                _error.value = e.message
            } finally {

            }
        }
    }

    fun updateTaskById(taskId: UUID) {
       viewModelScope.launch {
           val res = taskRepository.getTaskById(taskId)
           if (res == null) {
               _error.value = "No such data"
           } else {
               _task.value = res
               updateTaskFields()
           }
       }
    }

    private fun updateTaskFields() {
        setTitle(_task.value?.title)
        _task.value?.startTime?.let { setStartTime(it) }
        setEndTime(_task.value?.endTime)
        setDescription(_task.value?.description)
        _task.value?.isAnchor?.let { setIsAnchor(it) }
        _task.value?.isReminder?.let { setIsReminder(it) }
        setTag(_task.value?.tag)
        date.value = _task.value?.startTime
    }

    fun setTitle(titleText: String?) {
        if (titleText.isNullOrBlank()) {
            _titleInputError.value = R.string.title_empty_error
        } else {
            title.value = titleText!!
        }
    }

    fun setStartHourMinutes(newHour: Int, newMinute: Int, oldHour: Int, oldMinute: Int) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        if (startTime.value != null) {
            calendar.timeInMillis = startTime.value as Long
            calendar.add(Calendar.HOUR_OF_DAY, TimeCountingUtils.changedValue(oldHour, newHour))
            calendar.add(Calendar.MINUTE, TimeCountingUtils.changedValue(oldMinute, newMinute))
            startTime.value = calendar.timeInMillis
        }
    }

    fun setEndHourMinutes(newHour: Int, newMinute: Int, oldHour: Int, oldMinute: Int) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        if (endTime.value != null) {
            calendar.timeInMillis = endTime.value as Long
            calendar.add(Calendar.HOUR_OF_DAY, TimeCountingUtils.changedValue(oldHour, newHour))
            calendar.add(Calendar.MINUTE, TimeCountingUtils.changedValue(oldMinute, newMinute))
            endTime.value = calendar.timeInMillis
        }
    }

    fun setStartTime(startTimeMillis: Long) {
        startTime.value = startTimeMillis
    }

    fun setEndTime(endTimeMillis: Long?) {
        endTime.value = endTimeMillis
    }

    fun validateTime() {
        val s = startTime.value
        val e = endTime.value

        if (isReminder.value == false && s != null && e != null) {
            if (s >= e) {
                _startError.value = R.string.start_time_earlier_error
            } else if (isTaskOverlapsExistingTasks(s, e)) {
                _startError.value = R.string.time_overlapping_error
                _endError.value = R.string.time_overlapping_error
            }
        }
    }

    private fun isTaskOverlapsExistingTasks(newS: Long, newE: Long): Boolean {
        return _tasks.value?.stream()
            ?.anyMatch {
                val oldE = it.endTime
                val oldS = it.startTime
                var equalIds = false
                it.id?.let { listId ->
                    equalIds = listId == _task.value?.id
                }

                if (oldE == null || equalIds) false else TimeCountingUtils
                    .areTimePeriodsOverlap(newS, newE, oldS, oldE)
            } ?: false
    }

    fun setDescription(descriptionText: String?) {
        description.value = descriptionText
    }

    fun setIsAnchor(isAnchorBool: Boolean) {
        isAnchor.value = isAnchorBool
    }

    fun setIsReminder(isReminderBool: Boolean) {
        isReminder.value = isReminderBool
    }

    fun setTag(tagInstance: Tag?) {
        tag.value = tagInstance
    }
}