package com.open.day.dayscheduler.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.open.day.dayscheduler.R
import com.open.day.dayscheduler.data.repository.TaskRepository
import com.open.day.dayscheduler.data.repository.UserRepository
import com.open.day.dayscheduler.exception.NoSuchRowException
import com.open.day.dayscheduler.model.Tag
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.model.UserModel
import com.open.day.dayscheduler.util.TimeCountingUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskCreationViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
    ) : ViewModel() {

    private val _tasks: MutableLiveData<List<TaskModel>> = MutableLiveData()
    val tasks: LiveData<List<TaskModel>> = _tasks
    private lateinit var tasksForDate: List<TaskModel>

    private val _task: MutableLiveData<TaskModel?> = MutableLiveData()

    //TODO: remove all LiveData objects below and leave only LiveData<TaskModel>
    // to increase performance

    val title: MutableLiveData<String> = MutableLiveData()
    private val _date: MutableLiveData<Long> = MutableLiveData()
    val date: LiveData<Long> = _date
    private val _taskDate: MutableLiveData<Long> = MutableLiveData()
    val taskDate: LiveData<Long> = _taskDate
    val startTime: MutableLiveData<Long> = MutableLiveData()
    val endTime: MutableLiveData<Long?> = MutableLiveData()
    val isAnchor: MutableLiveData<Boolean> = MutableLiveData()
    val isReminder: MutableLiveData<Boolean> = MutableLiveData()
    val description: MutableLiveData<String?> = MutableLiveData()
    val tag: MutableLiveData<Tag?> = MutableLiveData()
    private val _addUsers: MutableLiveData<MutableSet<UserModel>> = MutableLiveData()
    val addUsers: LiveData<MutableSet<UserModel>> = _addUsers
    val isLocalTask: MutableLiveData<Boolean> = MutableLiveData()
    private val _isSignedIn: MutableLiveData<Boolean> = MutableLiveData()
    val isSignedIn: LiveData<Boolean> = _isSignedIn

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error
    private val _startError: MutableLiveData<Int> = MutableLiveData()
    val startError: LiveData<Int> = _startError
    private val _endError: MutableLiveData<Int> = MutableLiveData()
    val endError: LiveData<Int> = _endError
    private val _titleInputError: MutableLiveData<Int> = MutableLiveData()
    val titleInputError: LiveData<Int> = _titleInputError
    private val _addUserError: MutableLiveData<Int> = MutableLiveData()
    val addUserError: LiveData<Int> = _addUserError
    private val _spinner: MutableLiveData<Boolean> = MutableLiveData()
    val spinner: LiveData<Boolean> = _spinner

    init {
        _date.value = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
        _taskDate.value = _date.value
        _spinner.value = false
        _addUsers.value = mutableSetOf()
        isLocalTask.value = true
        setIsSignedIn()

        viewModelScope.launch {
            _tasks.value = taskRepository.getTasksList(TimeCountingUtils.getCurrentDayInterval())
        }
    }

    fun saveCurrentTask() {
        val titleString = title.value
        val startLong = startTime.value
        val rem = isReminder.value
        val anc = isAnchor.value
        val locTask = isLocalTask.value
        val addUsersIds = _addUsers.value


        if (titleString != null && startLong != null && rem != null && anc != null && locTask != null && addUsersIds != null) {
            val toSave = TaskModel(
                _task.value?.id,
                titleString,
                tag.value,
                startLong,
                rem,
                anc,
                endTime.value,
                description.value,
                addUsersIds,
                locTask
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
                _addUsers.value?.clear()
                isLocalTask.value = false
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
        launchDataLoad {
            //TODO: add exception handling
            _date.value?.let { date ->
                _tasks.value = taskRepository.getTasksList(TimeCountingUtils.getDayInterval(date))
            }
        }
    }

    private fun getLocalTasksForDate(date: Long?): Job {
        return viewModelScope.launch {
            date?.let {
                tasksForDate = taskRepository.getTasksList(TimeCountingUtils.getDayInterval(it))
            }
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
        setIsSignedIn()
        _task.value?.isTaskLocal.let { isLocalTask.value = it }
    }

    fun setTitle(titleText: String?) {
        if (titleText.isNullOrBlank()) {
            _titleInputError.value = R.string.field_empty_error
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

    /**
     * Should observe _spinner in the UI to wait till validation ends
     */
    fun validateTime() {
        val s = startTime.value
        val e = endTime.value
        var dayTime: Long = 0

        _taskDate.value?.let {
            dayTime = it
        }

        if (s != null) {
            //FIXME: separate logic based on isReminder
            if (TimeCountingUtils.isOutOfDayScope(dayTime, s, e)) {
                _startError.value = R.string.time_scope_error
                _endError.value = R.string.time_scope_error
            }
        }

        if (isReminder.value == false && s != null && e != null) {
            if (s >= e) {
                _startError.value = R.string.start_time_earlier_error
            }
            isTaskOverlapsExistingTasks(s, e)
        }
    }

    private fun isTaskOverlapsExistingTasks(newS: Long, newE: Long) {
        //TODO: refactor this logic
        _spinner.value = true
        val job = getLocalTasksForDate(_taskDate.value)

        job.invokeOnCompletion {
            val overlaps = tasksForDate.stream()
                .anyMatch {
                    val oldE = it.endTime
                    val oldS = it.startTime
                    var equalIds = false
                    it.id?.let { listId ->
                        equalIds = listId == _task.value?.id
                    }

                    if (oldE == null || equalIds) false else TimeCountingUtils
                        .areTimePeriodsOverlap(newS, newE, oldS, oldE)
                }

            if (overlaps) {
                _startError.value = R.string.time_overlapping_error
                _endError.value = R.string.time_overlapping_error
            }
            _spinner.value = false
        }
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

    fun setDate(date: Long) {
        _date.value = date
        updateLocalTasks()
    }

    fun setTaskDate(date: Long) {
        _taskDate.value = date
        val calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = date
        setStartTime(calendar.timeInMillis)
        calendar.add(Calendar.MINUTE, 1)
        setEndTime(calendar.timeInMillis)
    }

    fun setError(errorMsg: String) {
        _error.value = errorMsg
    }

    fun setAddUsers(email: String) {
        if (email.isNotBlank()) {
            viewModelScope.launch {
                try {
                    _addUsers.value?.add(userRepository.getUserByEmail(email))
                } catch (e: NoSuchRowException) {
                    _addUserError.value = R.string.user_not_found_error
                }
            }
        } else {
            _addUserError.value = R.string.field_empty_error
        }
    }

    fun removeFromAddUsers(email: String) {
        val userToDelete = _addUsers.value?.find { it.email == email }
        _addUsers.value?.remove(userToDelete)
    }

    private fun setIsSignedIn() {
        viewModelScope.launch {
            _isSignedIn.value = userRepository.getLocalUser().isSignedIn
        }
    }
}