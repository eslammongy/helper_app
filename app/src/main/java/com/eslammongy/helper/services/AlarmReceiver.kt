package com.eslammongy.helper.services
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.eslammongy.helper.R
import com.eslammongy.helper.ui.checklist.AddNewCheckList
import com.eslammongy.helper.ui.task.AddNewTask
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmReceiver:BroadcastReceiver() {

    private val channelName = "HelperChannel"
    private val channelID = "HelperChannelID"
    private val notificationID = 101
    override fun onReceive(context: Context?, intent: Intent?) {

        //val timeMillis = intent!!.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME , 0)
        val notifyMessage = intent!!.getStringExtra("NotifyMessage")
        val notifyHeader = intent.getStringExtra("NotifyHeader")

        when(intent.action){

            Constants.ACTION_SET_EXACT_ALARM -> {
                buildNotificationChannel(
                    context!!,
                    notifyHeader.toString(),
                    notifyMessage.toString(),
                   intent)
            }
            Constants.ACTION_SET_REPETITIVE_ALARM ->{
               val calender = Calendar.getInstance().apply {
                   this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)
               }
             //   AlarmService(context!!).setRepetitiveAlarm(calender.timeInMillis , notifyHeader!!,notifyMessage!! ,notifyKeyForm , elementID)
              //  buildNotificationChannel(context, convertDate(timeMillis) , notifyHeader.toString(), intent)
            }
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun buildNotificationChannel(context: Context, notifyHeader:String, message:String, intent: Intent?){
        val elementID = intent!!.getIntExtra("ElementNotifiedID" , 0)
        val notifyKeyForm = intent.getIntExtra("NotifiedFrom" , 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent:PendingIntent
        if (notifyKeyForm == 1){
           pendingIntent = PendingIntent.getActivity(context, 0, Intent(context , AddNewTask::class.java).apply {
                putExtra("NotifiedToTask" , notifyKeyForm)
                putExtra("TaskID" , elementID)
               Intent.FLAG_ACTIVITY_NEW_TASK
            }, PendingIntent.FLAG_UPDATE_CURRENT)
         //   Toast.makeText(context, "$elementID", Toast.LENGTH_SHORT).show()
        }else{
            pendingIntent = PendingIntent.getActivity(context, 0, Intent(context , AddNewCheckList::class.java).apply {
                putExtra("NotifiedToChList" , notifyKeyForm)
                putExtra("ChListID" , elementID)
                Intent.FLAG_ACTIVITY_NEW_TASK
            }, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationBuilder = NotificationCompat.Builder(context , channelID)
        notificationBuilder.setSmallIcon(R.drawable.ic_round_notifications_active_24)
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationBuilder.setContentTitle(notifyHeader)
        notificationBuilder.setContentText(message)
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.priority = NotificationCompat.PRIORITY_MAX
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (notificationManager.getNotificationChannel(channelID) == null){
                val channel = NotificationChannel(channelID , channelName ,
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                    lightColor = Color.GREEN
                    description = message
                    enableVibration(true)
                    enableLights(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        notificationManager.notify(notificationID , notificationBuilder.build())

    }

    private fun convertDate(timeMillis:Long):String = DateFormat.format("dd/MM/yyyy hh:mm:ss" , timeMillis).toString()
}