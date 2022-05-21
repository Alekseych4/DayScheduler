package com.open.day.dayscheduler

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.open.day.dayscheduler.viewModel.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

const val SIGN_IN_TYPE = "com.open.day.dayscheduler.SIGN_IN_TYPE"
const val SIGN_IN_GOOGLE = "com.open.day.dayscheduler.SIGN_IN_GOOGLE"
const val LOCAL_USAGE = "com.open.day.dayscheduler.LOCAL_USAGE"
const val MESSAGE_STRING = "com.open.day.dayscheduler.MESSAGE_RESOURCE_ID"

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    lateinit var signInClient: GoogleSignInClient
    lateinit var textView: TextView
    lateinit var signInBtn: MaterialButton
    private val RESULT_CODE = 1
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        textView = findViewById(R.id.local_usage_text)
        textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        signInBtn = findViewById(R.id.sign_in_button)

        signInBtn.setOnClickListener(onSignInBtnClickListener())
        textView.setOnClickListener(onTextClickListener())

        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestProfile()
            .build()

        signInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null) {
            startHostActivity(SIGN_IN_GOOGLE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE && resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            signInViewModel.updateUserWithGoogleAuth(account)
            startHostActivity(SIGN_IN_GOOGLE, resources.getString(R.string.welcome) + " " + account.displayName + "!")
        } catch (e: ApiException) {
            val errorMsg: String = e.localizedMessage ?: resources.getString(R.string.sign_in_error)
            Snackbar.make(textView, errorMsg, Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun startHostActivity(signInType: String, errorStringRes: String? = null) {
        val intent = Intent(this, HostActivity::class.java)
        intent.putExtra(SIGN_IN_TYPE, signInType)

        if (errorStringRes != null)
            intent.putExtra(MESSAGE_STRING, errorStringRes)

        startActivity(intent)
    }

    private fun onSignInBtnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val intent = signInClient.signInIntent
            startActivityForResult(intent, RESULT_CODE)
        }
    }

    private fun onTextClickListener(): View.OnClickListener {
        return View.OnClickListener {
            startHostActivity(LOCAL_USAGE, resources.getString(R.string.local_usage_prompt))
        }
    }
}