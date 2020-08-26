package com.example.easyfindapp.models

data class CompleteProfileModel(
    val user_name: String,
    val email_address: String,
    val age: String,
    val gender: String,
    val position: String,
    val photo_url: String,
    val skills: MutableList<String>,
    val role: String
)