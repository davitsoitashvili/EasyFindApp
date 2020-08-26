package com.example.easyfindapp.models

import com.google.gson.annotations.SerializedName

data class EmployerPersonUserModel(
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("email_address")
    val emailAddress: String,
    @SerializedName("age")
    val age: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("photo_url")
    val photoUrl: String,
    @SerializedName("role")
    val role: String
)

