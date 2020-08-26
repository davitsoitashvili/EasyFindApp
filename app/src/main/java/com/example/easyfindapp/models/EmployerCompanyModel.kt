package com.example.easyfindapp.models

import com.google.gson.annotations.SerializedName

data class EmployerCompanyModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("company_name")
    val companyName: String,
    @SerializedName("company_logo")
    val companyLogo: String
)