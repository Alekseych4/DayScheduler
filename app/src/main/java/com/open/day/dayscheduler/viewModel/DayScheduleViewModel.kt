package com.open.day.dayscheduler.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.open.day.dayscheduler.data.repository.TaskRepository
import com.open.day.dayscheduler.data.repository.UserRepository
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.util.TimeCountingUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class DayScheduleViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _tasks: MutableLiveData<List<TaskModel>> = MutableLiveData()
    val tasks: LiveData<List<TaskModel>> = _tasks
    private lateinit var tasksForDate: List<TaskModel>

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    private val _date: MutableLiveData<Long> = MutableLiveData()
    val date: LiveData<Long> = _date

    init {
        _date.value = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis
    }

    fun setDate(date: Long) {
        _date.value = date
        updateLocalTasks()
    }

    fun updateLocalTasks() {
        launchDataLoad {
            //TODO: add exception handling
            _date.value?.let { date ->
                _tasks.value = taskRepository.getTasksList(TimeCountingUtils.getDayInterval(date))
            }
        }
    }

    fun setError(errorMsg: String) {
        _error.value = errorMsg
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
}