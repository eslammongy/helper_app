package com.eslammongy.helper.database
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converter {

    @TypeConverter
    fun fromBitMap(bitmap: Bitmap): ByteArray {

        val outPutStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
        return outPutStream.toByteArray()
    }

    @TypeConverter
    fun toBitMap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    @TypeConverter
    fun bitmapToString(bitmap: Bitmap):String?{
        val outPutStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
        val imageByte = outPutStream.toByteArray()
        val imageString = Base64.encodeToString(imageByte, Base64.DEFAULT)

        if (imageString == null){
            return null
        }else{
            return imageString
        }
    }

    @TypeConverter
    fun stringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            bitmap
        } catch (e: Exception) {
            e.message
            null
        }
    }

}