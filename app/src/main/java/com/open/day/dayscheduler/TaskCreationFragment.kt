package com.open.day.dayscheduler

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.open.day.dayscheduler.viewModel.TasksViewModel

class TaskCreationFragment : Fragment() {

    companion object {
        fun newInstance() = TaskCreationFragment()
    }

    private lateinit var viewModel: TasksViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.task_creation_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TasksViewModel::class.java)
        // TODO: Use the ViewModel
    }

}