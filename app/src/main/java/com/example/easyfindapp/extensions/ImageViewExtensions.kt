package com.example.easyfindapp.extensions

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.easyfindapp.App
import com.example.easyfindapp.R

fun ImageView.setImage(uri : Uri) {
    Glide.with(App.appInstance!!.applicationContext).load(uri).into(this)
}

fun ImageView.changeImage(change : Boolean, changeImageView : Int, defaultImageView: Int) {
    if (change) {
        setImageResource(changeImageView)
    } else {
        setImageResource(defaultImageView)
    }
}