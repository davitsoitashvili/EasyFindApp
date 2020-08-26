package com.example.easyfindapp.utils

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.easyfindapp.App
import com.example.easyfindapp.activities.CompleteProfileActivity
import com.example.easyfindapp.activities.HomeActivity
import com.example.easyfindapp.network.EndPoints
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import org.json.JSONObject


fun checkUserIsCompleted(id: String, activity: Activity) {
    ResponseLoader.getResponseWithID(
        EndPoints.IS_USER_COMPLETED,
        id,
        null,
        object : ResponseCallback {
            override fun onSuccess(response: String) {
                val jsonResponse = JSONObject(response)
                if (jsonResponse.has("iscompleted")) {
                    val intent: Intent =
                        if (jsonResponse.get("iscompleted") == false) {
                            Intent(
                                App.appInstance!!.applicationContext,
                                CompleteProfileActivity::class.java
                            )
                        } else {
                            Intent(
                                App.appInstance!!.applicationContext,
                                HomeActivity::class.java
                            )
                        }
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    ContextCompat.startActivity(
                        App.appInstance!!.applicationContext,
                        intent,
                        null
                    )
                    activity.overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                }
            }

            override fun onFailure(response: String) {
                Toast.makeText(activity, response, Toast.LENGTH_LONG).show()
            }

            override fun onError(response: String) {
                Toast.makeText(activity, response, Toast.LENGTH_LONG).show()
            }
        })
}
