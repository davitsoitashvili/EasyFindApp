package com.example.easyfindapp.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.easyfindapp.R
import com.example.easyfindapp.extensions.setImage
import com.example.easyfindapp.utils.IMAGE_URL
import kotlinx.android.synthetic.main.activity_full_screen_image.*

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        init()
    }

    private fun init() {
        fullScreenImageView.setImage(Uri.parse(intent.getStringExtra(IMAGE_URL)))
    }
}