package com.example.easyfindapp.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun checkReadExternalStoragePermission(context: Context) = ActivityCompat.checkSelfPermission(
    context,
    android.Manifest.permission.READ_EXTERNAL_STORAGE
) == PackageManager.PERMISSION_GRANTED


fun checkWriteExternalStoragePermission(context: Context) = ActivityCompat.checkSelfPermission(
    context,
    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
) == PackageManager.PERMISSION_GRANTED


fun checkCameraPermission(context: Context) = ActivityCompat.checkSelfPermission(
    context,
    android.Manifest.permission.CAMERA
) == PackageManager.PERMISSION_GRANTED



