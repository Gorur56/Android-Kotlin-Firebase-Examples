package com.example.androidvideocallwithwebrtcandfirebasebackup.utils

import androidx.appcompat.app.AppCompatActivity
import com.example.androidvideocallwithwebrtcandfirebasebackup.R
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX


fun AppCompatActivity.getCameraAndMicPermission( success:() -> Unit) {
    PermissionX.init(this)
        .permissions(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
        .request { allGranted, _, _ ->
            if (allGranted) {
                success()
            } else {
                Snackbar.make(
                    findViewById(com.permissionx.guolindev.R.id.content),
                    "Camera and mic permission is required",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
}