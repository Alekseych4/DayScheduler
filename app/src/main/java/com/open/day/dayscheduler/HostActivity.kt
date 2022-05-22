package com.open.day.dayscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.open.day.dayscheduler.ui.DayScheduleFragment
import com.open.day.dayscheduler.ui.TaskCreationFragmentDirections
import com.open.day.dayscheduler.viewModel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        val container = findViewById<FragmentContainerView>(R.id.nav_host_fragment_container)

//        val navController = this.findNavController(R.id.nav_host_fragment_container)
        val viewModel: TasksViewModel by viewModels()
        viewModel.error.observe(this) {
            Snackbar.make(container, it, Snackbar.LENGTH_LONG).show()
        }
    }
}