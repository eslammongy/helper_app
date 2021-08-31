package com.eslammongy.helper.services
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import com.eslammongy.helper.R
import com.eslammongy.helper.ui.module.checklist.AddNewCheckList
import com.eslammongy.helper.ui.module.task.AddNewTask
import com.eslammongy.helper.utilis.notifyMessage
import io.karn.notify.Notify
import io.karn.notify.internal.utils.Action
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val timeMillis = intent!!.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME , 0)
        val notifyMessage = intent.getStringExtra("NotifyMessage")
        val elementID = intent.getIntExtra("ElementNotifiedID" , 0)
        val notifyKeyForm = intent.getIntExtra("NotifiedFrom" , 0)

        when(intent.action){

            Constants.ACTION_SET_EXACT_ALARM -> {
                buildNotification(
                    context!!,
                    notifyMessage.toString(),
                    notifyKeyForm , elementID
                )
            }
            Constants.ACTION_SET_REPETITIVE_ALARM ->{
               val calender = Calendar.getInstance().apply {
                   this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)
               }
                AlarmService(context!!).setRepetitiveAlarm(calender.timeInMillis ,notifyMessage!! , notifyKeyForm , elementID)
                buildNotification(context, convertDate(timeMillis) , notifyKeyForm ,elementID)
            }
        }

    }

    private fun buildNotification(context: Context , message:String , key:Int , elementID:Int){
        val intent = Intent(context, AddNewTask::class.java).apply {
            // putExtra("NotifiedToCheckList" , key)
            putExtra("TaskNotifiedID" , elementID)
        }
        Notify.with(context)

            .header {
                headerText = "Notify"
            }
            .asBigText {
                text = null
                title = notifyMessage()
                bigText = message

            }
            .actions {
                if (key == 1){
                    add(
                        Action(
                            R.drawable.ic_calendar,
                            "Show",
                            PendingIntent.getActivity(context, 0, intent,0)
                        )
                    )
                }else{
                    add(
                        Action(
                            R.drawable.ic_iconfinder_check_list,
                            "Show",
                            PendingIntent.getActivity(context, 0, Intent(context, AddNewCheckList::class.java).apply {
                                putExtra("NotifiedToCheckList" , key)
                                putExtra("SubChlID" , elementID)
                            }, 0)
                        )
                    )
                }

            }
            .show()
    }

    private fun convertDate(timeMillis:Long):String = DateFormat.format("dd/MM/yyyy hh:mm:ss" , timeMillis).toString()
}