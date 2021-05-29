package com.eslammongy.helper.database.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converter {

    @TypeConverter
    fun fromBitMap(bitmap: Bitmap):ByteArray{
        val outPutStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG , 100 , outPutStream)
        return outPutStream.toByteArray()
    }

    @TypeConverter
    fun toBitMap(byteArray: ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(byteArray , 0 , byteArray.size)
    }
}