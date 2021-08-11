package com.eslammongy.helper.helpers

import android.graphics.Bitmap
import android.util.Log
import kotlin.math.roundToInt
import kotlin.math.sqrt
object CompressImage {

    fun reduceBitmapSize(bitmap: Bitmap, MAX_SIZE: Int): Bitmap {
        val ratioSquare: Double
        val bitmapHeight: Int = bitmap.height
        val bitmapWidth: Int = bitmap.width
        ratioSquare = (bitmapHeight * bitmapWidth / MAX_SIZE).toDouble()
        if (ratioSquare <= 1) return bitmap
        val ratio = sqrt(ratioSquare)
        Log.d("mylog", "Ratio: $ratio")
        val requiredHeight = (bitmapHeight / ratio).roundToInt()
        val requiredWidth = (bitmapWidth / ratio).roundToInt()
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true)
    }

    fun generateThumb(bitmap: Bitmap, THUMB_SIZE: Int): Bitmap {
        val ratioSquare: Double
        val bitmapHeight: Int = bitmap.height
        val bitmapWidth: Int = bitmap.width
        ratioSquare = (bitmapHeight * bitmapWidth / THUMB_SIZE).toDouble()
        if (ratioSquare <= 1) return bitmap
        val ratio = sqrt(ratioSquare)
        Log.d("mylog", "Ratio: $ratio")
        val requiredHeight = (bitmapHeight / ratio).roundToInt()
        val requiredWidth = (bitmapWidth / ratio).roundToInt()
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true)
    }
}