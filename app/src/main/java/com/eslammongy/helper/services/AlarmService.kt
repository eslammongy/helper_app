package com.eslammongy.helper.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmService(private val context: Context) {

    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setExactAlarm(timeMillis: Long ,notifyHeader:String  ,message:String ,  notifiedFrom:Int , elementID:Int) {

        setAlarm(timeMillis , getPendingIntent(getIntent(notifyHeader , message ,notifiedFrom , elementID).apply {
            action = Constants.ACTION_SET_EXACT_ALARM
            putExtra(Constants.EXTRA_EXACT_ALARM_TIME , timeMillis)
        }))

    }

    fun setRepetitiveAlarm(timeMillis: Long ,notifyHeader:String ,message:String , notifiedFrom:Int , elementID:Int) {
        setAlarm(timeMillis , getPendingIntent(getIntent(notifyHeader , message , notifiedFrom , elementID).apply {
            action = Constants.ACTION_SET_REPETITIVE_ALARM
            putExtra(Constants.EXTRA_EXACT_ALARM_TIME , timeMillis)
        }))
    }

    private fun setAlarm(timeMillis: Long, pendingIntent: PendingIntent) {

        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent
                )
            } else {

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent)
            }
        }
    }

    private fun getIntent(notifyHeader:String , message:String , notifiedFrom:Int , elementID:Int):
            Intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("NotifyHeader" , notifyHeader)
        putExtra("NotifyMessage" , message)
        putExtra("NotifiedFrom" , notifiedFrom)
        putExtra("ElementNotifiedID" , elementID)
    }

    private fun getPendingIntent(intent: Intent): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            RandomIntRequest.getRandomIntNumber(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}