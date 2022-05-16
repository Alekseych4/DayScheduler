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

    init {
        viewModelScope.launch {
            taskRepository.getTasks(TimeCountingUtils.getCurrentDayInterval())
                .collect { _tasks.value = it }
        }
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
            } finally {

            }
        }
    }
}