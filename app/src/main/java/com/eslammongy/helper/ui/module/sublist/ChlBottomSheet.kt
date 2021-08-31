package com.eslammongy.helper.ui.module.sublist
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.SubCheckList
import com.eslammongy.helper.databinding.FragmentChlBottomSheetBinding
import com.eslammongy.helper.utilis.setToastMessage
import com.eslammongy.helper.services.AlarmService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class ChlBottomSheet(parentChlID:Int , parentChlTitle:String) : BottomSheetDialogFragment(),View.OnClickListener  , CoroutineScope{

    private var _binding: FragmentChlBottomSheetBinding? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    private val binding get() = _binding!!
    private var parentChlID: Int = 0
    private var isComplete:Boolean = false
    private var subChlColor: Int? = null
    private var parentChlTitle:String? = null
    private var chlAlarm:Long = 0L
    private lateinit var alarmService: AlarmService

    init {
        this.parentChlID = parentChlID
        this.parentChlTitle = parentChlTitle
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChlBottomSheetBinding.inflate(inflater , container , false)
        job = Job()
        alarmService = AlarmService(requireContext())
        binding.saveSubChl.setOnClickListener(this)
        binding.setSubChlCalender.setOnClickListener(this)
        subChlColor = ResourcesCompat.getColor(resources, R.color.ColorDefaultNote, requireActivity().theme)
        binding.chlPaletteColor.setOnColorSelectedListener { clr -> subChlColor = clr }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        sheetContainer.layoutParams.height = height
        sheetContainer.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.saveSubChl ->{saveNewSubChl()}
            R.id.setSubChlCalender ->setAlarm ()

        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun setAlarm() {
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
                            chlAlarm = this.timeInMillis
                            val timeFormatted = SimpleDateFormat("hh:mm a").format(this.time)
                            binding.subChlTime.text = timeFormatted.toString()
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()

//                    val dateFormatted =
//                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(this.time)
//                    dateTask = dateFormatted.toString()
//                    binding.tvSheetDate.text = dateTask

                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    private fun saveNewSubChl(){

        val title = binding.subChlTitle.text.toString()
        val time = binding.subChlTime.text.toString()
       val subCheckList = SubCheckList(title , time , subChlColor.toString() , isComplete , parentChlID)

        if (title.isEmpty() || time.isEmpty()){
            requireActivity().setToastMessage("Error required filed is empty.")
        }else{
            launch {
                HelperDataBase.getDataBaseInstance(requireContext()).checkListDao().saveNewSubCheckList(subCheckList)
                alarmService.setExactAlarm(chlAlarm , "You Have A New Task In Your ToDo List Called $parentChlTitle .. Let's Go To Do It." , 2 , parentChlID)
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
        _binding = null
    }

}