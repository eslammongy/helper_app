package com.eslammongy.helper.ui.sublist
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.eslammongy.helper.R
import com.eslammongy.helper.database.entities.SubCheckList
import com.eslammongy.helper.databinding.FragmentChlBottomSheetBinding
import com.eslammongy.helper.services.AlarmService
import com.eslammongy.helper.utilis.setToastMessage
import com.eslammongy.helper.viewModels.ChListViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class ChlBottomSheet(parentChlID:Int , parentChlTitle:String) : BottomSheetDialogFragment(),View.OnClickListener {

    private var _binding: FragmentChlBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var chListViewModel: ChListViewModel

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

        chListViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(ChListViewModel::class.java)
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

        }
    }
    private fun saveNewSubChl(){

        val title = binding.subChlTitle.text.toString()
        val time = binding.subChlTime.text.toString()
       val subCheckList = SubCheckList(title , time , subChlColor.toString() , isComplete , parentChlID)

        if (title.isEmpty() || time.isEmpty()){
            requireActivity().setToastMessage("Please make sure all fields are filled" , Color.RED)
        }else{
            chListViewModel.saveNewSubChList(subCheckList)
            alarmService.setExactAlarm(chlAlarm , binding.subChlTitle.text.toString(),"You have a new task called ${subCheckList.subChl_Title} in your checkList called $parentChlTitle .. let's go to do it." , 2 , parentChlID)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}