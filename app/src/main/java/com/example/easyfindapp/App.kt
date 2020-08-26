package com.example.easyfindapp

import android.app.Application

class App : Application() {

    companion object {
        var appInstance : App? = null
    }
    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}