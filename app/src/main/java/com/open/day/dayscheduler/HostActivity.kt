package com.open.day.dayscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.open.day.dayscheduler.ui.DayScheduleFragment
import com.open.day.dayscheduler.ui.TaskCreationFragmentDirections
import com.open.day.dayscheduler.viewModel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottom_app_bar)
        val fab = findViewById<FloatingActionButton>(R.id.add_save_new_task_fab)

        val navController = this.findNavController(R.id.nav_host_fragment_container)
        val viewModel: TasksViewModel by viewModels()

        fab.setOnClickListener(onAddNewTaskClickListener(navController, bottomAppBar, viewModel))
    }

    private fun onAddNewTaskClickListener(navController: NavController,
                                          appBar: BottomAppBar,
                                          viewModel: TasksViewModel): View.OnClickListener {
        return View.OnClickListener {
            val fab = it as FloatingActionButton
            when (navController.currentDestination?.id) {
                R.id.dayScheduleFragment -> {
                    navController.navigate(TaskCreationFragmentDirections.actionGlobalTaskCreationFragment(null))
                    appBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                    fab.setImageResource(R.drawable.ic_save_task_24)
                }
                R.id.taskCreationFragment -> {
                    val entry = navController.getBackStackEntry(R.id.dayScheduleFragment)

                    navController.navigate(TaskCreationFragmentDirections.actionGlobalTaskCreationFragment(null))
                    appBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                    fab.setImageResource(R.drawable.ic_create_chip_24)
                }
                else -> {

                }
            }
        }
    }
}