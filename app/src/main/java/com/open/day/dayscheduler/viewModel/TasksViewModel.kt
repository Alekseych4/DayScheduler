package com.open.day.dayscheduler.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.open.day.dayscheduler.data.repository.TaskRepository
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.util.TimeCountingUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {

    private val _tasks: MutableLiveData<List<TaskModel>> = MutableLiveData()
    val tasks: LiveData<List<TaskModel>> = _tasks

    val task: MutableLiveData<TaskModel?> = MutableLiveData()

    val title: MutableLiveData<String?> = MutableLiveData()
    val startTime: MutableLiveData<Long?> = MutableLiveData()
    val endTime: MutableLiveData<Long?> = MutableLiveData()
    val isAnchor: MutableLiveData<Boolean?> = MutableLiveData()
    val isReminder: MutableLiveData<Boolean?> = MutableLiveData()
    val description: MutableLiveData<String?> = MutableLiveData()

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error
    private val _spinner: MutableLiveData<Boolean> = MutableLiveData()
    val spinner: LiveData<Boolean> = _spinner

    init {
        viewModelScope.launch {
            taskRepository.getTasks(TimeCountingUtils.getCurrentDayInterval())
                .collect { _tasks.value = it }
        }
    }

    fun saveCurrentTask() {
        val toSave = task.value
        if (toSave != null)
            viewModelScope.launch { taskRepository.saveTask(toSave) }
    }

    fun saveTask(taskModel: TaskModel) {
        viewModelScope.launch { taskRepository.saveTask(taskModel) }
    }

    fun deleteTask(id: UUID) {
        viewModelScope.launch { taskRepository.deleteTaskById(id) }
    }

    fun updateLocalTasks() {
        launchDataLoad { taskRepository.getTasks(TimeCountingUtils.getCurrentDayInterval()) }
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
               task.value = res
           }
       }
    }
}