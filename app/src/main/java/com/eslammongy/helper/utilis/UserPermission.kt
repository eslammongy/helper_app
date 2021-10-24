package com.eslammongy.helper.utilis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage


@SuppressLint("MissingPermission")
class UserPermission(var activity: Activity?) {


    val openGalleryAndGetImage = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(90)
                .getIntent(activity!!)
        }
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri

        }

    }

    fun checkUserLocationPermission(permissions: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                permissions) != PackageManager.PERMISSION_GRANTED) {
            return false
        }

        return true
    }

     fun showRequestPermissionDialog(permissions: String, name: String, requestCode: Int) {

        val builder = AlertDialog.Builder(activity!!)
        builder.apply {
            setMessage("You need to access your $name permission is required to use this app")
            setTitle("Permission Required")
            setPositiveButton("Ok") { _, _ ->
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(permissions),
                    requestCode
                )
            }
        }
        val dialog = builder.create()
        dialog.show()
    }


}
