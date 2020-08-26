package com.example.easyfindapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class PostModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("create_date")
    val createDate: String,
    @SerializedName("experience_level")
    val experienceLevel: String,
    @SerializedName("skills")
    val skills: MutableList<String>,
    @SerializedName("photo_url")
    val photoUrl: String,
    @SerializedName("email_address")
    val emailAddress: String?,
    @SerializedName("postID")
    val postID: Int?
) : Parcelable