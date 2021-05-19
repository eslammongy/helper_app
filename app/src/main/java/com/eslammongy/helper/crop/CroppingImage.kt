package com.eslammongy.helper.crop

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.eslammongy.helper.R
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*


 class PickAndCropImage(private val currentActivity: Activity, private val pickImageCode:Int){

    fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        currentActivity.startActivityForResult(intent, pickImageCode)

    }



    fun startCropImage(imageUri: Uri) {

        val destinationFileName =
            StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
        val uCropImage = UCrop.of(imageUri, Uri.fromFile(File(currentActivity.cacheDir, destinationFileName)))
        uCropImage.withAspectRatio(1F, 1F)
        uCropImage.withAspectRatio(16F, 9F)
        uCropImage.useSourceImageAspectRatio()
        uCropImage.withMaxResultSize(450, 450)
        uCropImage.withOptions(getCropOptions())
        uCropImage.start(currentActivity)

    }

    private fun getCropOptions(): UCrop.Options {

        val uCropOption = UCrop.Options()
        uCropOption.setCompressionQuality(80)
        uCropOption.setCompressionFormat(Bitmap.CompressFormat.PNG)
        uCropOption.setCompressionFormat(Bitmap.CompressFormat.JPEG)

        uCropOption.setHideBottomControls(false)
        uCropOption.setFreeStyleCropEnabled(true)
        uCropOption.setStatusBarColor(currentActivity.resources.getColor(R.color.colorDark))
        uCropOption.setToolbarColor(currentActivity.resources.getColor(R.color.ColorDefaultNote))

        uCropOption.setToolbarTitle("cropping image")

        return uCropOption;

    }
}