package com.example.easyfindapp.models

import com.google.gson.annotations.SerializedName

data class SignInResponseModel(
    @SerializedName("userID")
    val userID: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("role")
    val role: String,
    @SerializedName("employertype")
    val employerType : String?,
    @SerializedName("email_address")
    val emailAddress: String,
    val photo_url:String

)