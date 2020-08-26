package com.example.easyfindapp.models.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostViewModel : ViewModel() {
    private val selectedExperienceLevel: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun setValue(value: String) {
        selectedExperienceLevel.value = value
    }

    fun getValue(): MutableLiveData<String> {
        return selectedExperienceLevel
    }
}