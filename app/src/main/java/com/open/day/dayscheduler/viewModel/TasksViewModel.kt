package com.open.day.dayscheduler.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.open.day.dayscheduler.data.repository.TaskRepository
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.util.TimeCountingUtils
import kotlinx.coroutines.launch
import java.util.stream.Collectors
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val tasks: MutableLiveData<List<TaskModel>> by lazy {
        MutableLiveData<List<TaskModel>>().also {
            loadTasks()
        }
    }

    fun getTasks(): LiveData<List<TaskModel>> {
        return tasks
    }

    private fun loadTasks() {
        viewModelScope.launch {

        }
    }
}