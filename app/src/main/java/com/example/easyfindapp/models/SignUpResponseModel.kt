package com.example.easyfindapp.models

import com.google.gson.annotations.SerializedName

data class SignUpResponseModel(
    @SerializedName("registered")
    val registered : Boolean,
    @SerializedName("userID")
    val userID : String,
    @SerializedName("status")
    val status : String
)