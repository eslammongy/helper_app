package com.eslammongy.helper.fragment.dialogs

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.SubCheckList
import com.eslammongy.helper.databinding.FragmentChlBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class ChlBottomSheet(parentChlID:Int) : BottomSheetDialogFragment(),View.OnClickListener {

    private var _binding: FragmentChlBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var parentChlID: Int = 0
    private var isComplete:Boolean = false
    private var subChlColor: Int? = null


    init {
        this.parentChlID = parentChlID
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChlBottomSheetBinding.inflate(inflater , container , false)

        binding.saveSubChl.setOnClickListener(this)
        binding.subChlTime.setOnClickListener(this)
        subChlColor = resources.getColor(R.color.ColorDefaultNote)
        binding.chlPaletteColor.setOnColorSelectedListener { clr ->
            subChlColor = clr

        }

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
            R.id.subChlTime ->{openSubChlTimeDialog()}

        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun openSubChlTimeDialog() {
        val calender = Calendar.getInstance()
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(activity!!)
        val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calender.set(Calendar.MINUTE, minute)
            val timeFormatted = SimpleDateFormat("hh:mm a").format(calender.time)
            binding.subChlTime.text = timeFormatted.toString()
        }
        TimePickerDialog(
            activity!!, timePicker, calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE), isSystem24Hour
        ).show()
    }

    private fun saveNewSubChl(){

        val title = binding.subChlTitle.text.toString()
        val time = binding.subChlTime.text.toString()
       val subCheckList = SubCheckList(title , time , subChlColor.toString() , isComplete , parentChlID)

        if (title.isEmpty() || time.isEmpty()){
            Toast.makeText(
                activity!!,
                "Error required filed is empty.. ${title}!",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            HelperDataBase.getDataBaseInstance(activity!!).checkListDao().saveNewSubCheckList(subCheckList)
            dialog!!.dismiss()
        }

    }

}