package com.example.easyfindapp.user_preference

import android.content.Context
import com.example.easyfindapp.App

object UserPreference {
    const val USER_ID = "User ID"
    const val ROLE = "Role"
    const val EMAIL_ADDRESS = "Email Address"
    const val EMPLOYER_TYPE = "Employer Type"
    const val PHOTO_URL="photo"

    private val preference by lazy {
        App.appInstance!!.applicationContext.getSharedPreferences("User",Context.MODE_PRIVATE)
    }
    private val editor by lazy {
        preference.edit()
    }
    fun saveData(key : String, value : String) {
        editor.putString(key,value)
        editor.commit()
    }
    fun getData(key : String) = preference.getString(key, "")

    fun clearData() {
        editor.clear()
        editor.commit()
    }
}