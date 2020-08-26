package com.example.easyfindapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.easyfindapp.R
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.checkUserIsCompleted

class SplashActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }

    private fun init() {
        handler = Handler()
        runnable = Runnable {
            checkUser()
        }
    }

    private fun openAuthenticationActivity() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onResume() {
        handler.postDelayed(runnable, 2000)
        super.onResume()
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        super.onPause()
    }

    private fun checkUser() {
        if (UserPreference.getData(UserPreference.USER_ID)!!.isEmpty()) {
            openAuthenticationActivity()
        } else {
            checkUserIsCompleted(UserPreference.getData(UserPreference.USER_ID)!!, this)
        }
    }
}