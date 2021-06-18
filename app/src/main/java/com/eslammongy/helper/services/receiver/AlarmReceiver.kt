package com.eslammongy.helper.services.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import com.eslammongy.helper.R
import com.eslammongy.helper.services.AlarmService
import com.eslammongy.helper.services.Constants
import com.eslammongy.helper.ui.HomeActivity
import io.karn.notify.Notify
import io.karn.notify.internal.utils.Action
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val timeMillis = intent!!.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME , 0)
        val notifyMessage = intent.getStringExtra("NotifyMessage")
        val notifyComeForm = intent.getStringExtra("NotifyComeForm")
        val notifyKeyForm = intent.getIntExtra("NotifyKey" , 0)

        when(intent.action){

            Constants.ACTION_SET_EXACT_ALARM -> {
                buildNotification(
                    context!!,
                    "Hello My Pro",
                    notifyMessage.toString(),
                    notifyKeyForm
                )
            }
            Constants.ACTION_SET_REPETITIVE_ALARM ->{
               val calender = Calendar.getInstance().apply {
                   this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)
               }
                AlarmService(context!!).setRepetitiveAlarm(calender.timeInMillis ,notifyComeForm!! ,notifyMessage!! , notifyKeyForm)
                buildNotification(context,"Hello My Pro" , convertDate(timeMillis) , notifyKeyForm)
            }
        }

    }

    private fun buildNotification(context: Context , title:String , message:String , key:Int){

        Notify.with(context)

            .header {
                headerText = "Notify"
            }

            .asBigText {
                this.title = title
                bigText = message
            }
            .meta {
                clickIntent = if (key == 2)
                    PendingIntent.getActivity(context, 0, Intent(context, HomeActivity::class.java).apply {
                        putExtra("ArrowKey" , key)
                    }, 0)
                else
                    PendingIntent.getActivity(context, 0, Intent(context, HomeActivity::class.java).apply {
                        putExtra("ArrowKey" , key)
                    }, 0)

            }
            .actions {
             add(
                 Action(
                     R.drawable.ic_round_cancel_24,
                     "Show",
                     PendingIntent.getActivity(context, 0, Intent(context, HomeActivity::class.java), 0)
                 ))
            }

            .show()
    }

    private fun convertDate(timeMillis:Long):String = DateFormat.format("dd/MM/yyyy hh:mm:ss" , timeMillis).toString()
}