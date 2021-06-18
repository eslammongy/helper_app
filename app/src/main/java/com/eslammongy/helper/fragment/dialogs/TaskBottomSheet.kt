package com.eslammongy.helper.fragment.dialogs

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.FriendsAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.converter.Converter
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.FragmentTaskBottomSheetBinding
import com.eslammongy.helper.services.AlarmService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.O)
class TaskBottomSheet(
    taskColor: Int?,
    time: String,
    date: String,
    titleTask:String,
    link: String,
    friendName: String,
    friendImage: ByteArray
): BottomSheetDialogFragment(), View.OnClickListener ,FriendsAdapter.OnItemClickerListener{

    private var _binding: FragmentTaskBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var taskColor: Int? = null
    private var timeTask:String? = null
    private var dateTask:String? = null
    private var titleTask:String? = null
    private var link:String? = null
    private var friendName:String? = null
    private var friendImage:ByteArray? = null
    private var listMyFriends = ArrayList<ContactEntities>()
    private var friendAdapter: FriendsAdapter? = null
    private lateinit var alarmService: AlarmService

    init {
        this.taskColor = taskColor
        this.timeTask = time
        this.dateTask = date
        this.titleTask = titleTask
        this.link = link
        this.friendName = friendName
        this.friendImage = friendImage
    }
    private lateinit var sheetListener: BottomSheetInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBottomSheetBinding.inflate(inflater, container, false)

        alarmService = AlarmService(requireContext())
        binding.enterLinkText.setText(link)
        binding.chlPaletteColor.setSelectedColor(taskColor!!)
        displayFriendsRecyclerView()
        binding.tvSheetTime.text = timeTask
        binding.tvSheetDate.text = dateTask
        binding.saveTaskInfo.setOnClickListener(this)
        binding.tvSheetDate.setOnClickListener(this)
        binding.tvSheetTime.setOnClickListener(this)
        binding.chlPaletteColor.setOnColorSelectedListener { clr ->
            taskColor = clr

        }
        return binding.root
    }


    override fun onStart() {
        super.onStart()

        val sheetContainer = requireView().parent as? ViewGroup ?: return
        //val height = (resources.displayMetrics.heightPixels * 0.95).toInt()
        val height = (resources.displayMetrics.heightPixels)
        sheetContainer.layoutParams.height = height
        sheetContainer.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.saveTaskInfo -> {
              getTaskInfoFromBottomSheet()
            }
            R.id.tv_SheetDate -> {
                setAlarm { alarmService.setExactAlarm(it ,titleTask.toString() ,"You Have A New Task With Your Friend Named $friendName .. Let's Go To Do It." , 1) }
            }
            R.id.tv_SheetTime -> {
                //openTaskTimeDialog()
                setAlarm { alarmService.setExactAlarm(it , titleTask.toString(),"You Have A New Task With Your Friend Named $friendName .. Let's Go To Do It." , 1) }

            }
        }
    }

    private fun displayFriendsRecyclerView(){
        listMyFriends = HelperDataBase.getDataBaseInstance(requireContext()).contactDao().getAllContacts() as ArrayList<ContactEntities>
        friendAdapter = FriendsAdapter(requireContext() , listMyFriends, this)
        binding.taskFiendRecycler.setHasFixedSize(true)
        binding.taskFiendRecycler.layoutManager = LinearLayoutManager(context)
        binding.taskFiendRecycler.adapter = friendAdapter

    }

    private fun getTaskInfoFromBottomSheet(){
        if (checkValidateUrl() || binding.enterLinkText.text!!.isEmpty()) {
            val selectedColor = this.taskColor
            val link = binding.enterLinkText.text.toString()
            val time = binding.tvSheetTime.text.toString()
            val date = binding.tvSheetDate.text.toString()
            sheetListener.setTaskInfo(
                selectedColor.toString(), link, time, date,
                friendName!!, friendImage!!
            )
            dismiss()
        } else {
            binding.enterLinkText.error = "Url is not valid!!"
            Toast.makeText(
                requireContext(),
                "please make sure that the url is valid or not.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun checkValidateUrl():Boolean{
        val linkSample = ("((http|https)://)(www.)?"
                + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%"
                + "._\\+~#?&//=]*)")

        val uRL: Pattern = Pattern.compile(linkSample)

        if (uRL.matcher(binding.enterLinkText.text.toString()).matches()) {
             return true
        }

        return false
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            sheetListener = context as BottomSheetInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement ExampleDialogListener"
            )
        }
    }

    /*
    private fun openTaskDateDialog() {

        val calender = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->

                calender.set(Calendar.YEAR, year)
                calender.set(Calendar.MONTH, month)
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormatted =
                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(calender.time)

                binding.tvSheetDate.text = dateFormatted

            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

     */

    @SuppressLint("SimpleDateFormat")
    private fun setAlarm(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                requireContext(),
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        requireContext(),
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                            val timeFormatted = SimpleDateFormat("hh:mm a").format(this.time)
                            timeTask = timeFormatted.toString()
                            binding.tvSheetTime.text = timeTask
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()

                    val dateFormatted =
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(this.time)
                    dateTask = dateFormatted.toString()
                    binding.tvSheetDate.text = dateTask

                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    /*
    @SuppressLint("SimpleDateFormat")
    private fun openTaskTimeDialog() {
        val calender = Calendar.getInstance()
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(activity)
        val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calender.set(Calendar.MINUTE, minute)
            val timeFormatted = SimpleDateFormat("hh:mm a").format(calender.time)
            binding.tvSheetTime.text = timeFormatted.toString()

            alarmService.setExactAlarm(calender.timeInMillis)
        }
        TimePickerDialog(
            requireContext(), timePicker, calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE), isSystem24Hour
        ).show()
    }

     */

    interface BottomSheetInterface {

        fun setTaskInfo(
            color: String,
            lintText: String,
            time: String,
            date: String,
            friendName: String,
            friendImage: ByteArray
        )
    }

    override fun onClicked(contactEntities: ContactEntities, position: Int) {

        friendName = contactEntities.contact_Name
        val convertImage = Converter().fromBitMap(contactEntities.contact_Image!!)
        friendImage = convertImage
       // Toast.makeText(requireContext(), "You Select Your Friend Named $friendName", Toast.LENGTH_LONG).show()
    }

    override fun onItemClickListener(position: Int, view: View?) {
        friendAdapter!!.selectedItem()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}