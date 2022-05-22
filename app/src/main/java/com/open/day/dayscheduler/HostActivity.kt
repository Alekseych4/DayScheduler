package com.open.day.dayscheduler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.snackbar.Snackbar
import com.open.day.dayscheduler.viewModel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        val container = findViewById<FragmentContainerView>(R.id.nav_host_fragment_container)

        val viewModel: TasksViewModel by viewModels()
//        val navController = this.findNavController(R.id.nav_host_fragment_container)

        val startType = intent.getStringExtra(SIGN_IN_TYPE)
        val message = intent.getStringExtra(MESSAGE_STRING)
        if (message != null) {
            viewModel.setError(message)
        }

        viewModel.error.observe(this) {
            Snackbar.make(container, it, Snackbar.LENGTH_LONG).show()
        }
    }
}