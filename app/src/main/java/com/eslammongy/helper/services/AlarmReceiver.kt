package com.eslammongy.helper.services
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import com.eslammongy.helper.R
import com.eslammongy.helper.ui.module.checklist.AddNewCheckList
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.ui.module.task.AddNewTask
import io.karn.notify.Notify
import io.karn.notify.internal.utils.Action
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val timeMillis = intent!!.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME , 0)
        val notifyMessage = intent.getStringExtra("NotifyMessage")
        val todoID = intent.getIntExtra("ToDoID" , 0)
        val notifyKeyForm = intent.getIntExtra("NotifiedFrom" , 0)

        when(intent.action){

            Constants.ACTION_SET_EXACT_ALARM -> {
                buildNotification(
                    context!!,
                    notifyMessage.toString(),
                    notifyKeyForm,todoID
                )
            }
            Constants.ACTION_SET_REPETITIVE_ALARM ->{
               val calender = Calendar.getInstance().apply {
                   this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)
               }
                AlarmService(context!!).setRepetitiveAlarm(calender.timeInMillis ,notifyMessage!! , notifyKeyForm , todoID)
                buildNotification(context, convertDate(timeMillis) , notifyKeyForm ,todoID)
            }
        }

    }

    private fun buildNotification(context: Context , message:String , key:Int , todoID:Int){

        Notify.with(context)

            .header {
                headerText = "Notify"
            }
            .asBigText {
                title = "Hello My Pro"
                bigText =message

            }
            .actions {
                if (key == 1){
                    add(
                        Action(
                            R.drawable.ic_calendar,
                            "Show",
                            PendingIntent.getActivity(context, 0, Intent(context, AddNewTask::class.java).apply {
                                putExtra("NotifiedTask" , key)
                                putExtra("NotifiedTaskID" , todoID)
                            }, 0)
                        )
                    )
                }else{
                    add(
                        Action(
                            R.drawable.ic_iconfinder_check_list,
                            "Show",
                            PendingIntent.getActivity(context, 0, Intent(context, AddNewCheckList::class.java).apply {
                                putExtra("NotifiedCheckList" , key)
                                putExtra("NotifiedCheckListID" , todoID)
                            }, 0)
                        )
                    )
                }

            }
            .show()
    }

    private fun convertDate(timeMillis:Long):String = DateFormat.format("dd/MM/yyyy hh:mm:ss" , timeMillis).toString()
}