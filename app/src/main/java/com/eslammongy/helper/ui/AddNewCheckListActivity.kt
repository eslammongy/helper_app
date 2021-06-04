package com.eslammongy.helper.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.SubChlAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.SubCheckList
import com.eslammongy.helper.databinding.ActivityAddNewCheckListBinding
import com.eslammongy.helper.fragment.dialogs.ChlBottomSheet
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddNewCheckListActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddNewCheckListBinding
    private var showing = false
    private var chlColor: Int? = null
    private var checkLId: Int = 0
    private var isComplete:Boolean = false
    private var listOfSubChl = ArrayList<SubCheckList>()
    private var subChlAdapter:SubChlAdapter? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCheckListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // receive data from adapter by id
        checkLId = intent.getIntExtra("chlID", 0)
        chlColor = resources.getColor(R.color.ColorDefaultNote)
        displayInfoFromAdapter()
        displaySubCheckList()
        binding.btnArrowToHome.setOnClickListener(this)
        binding.btnAddNewChl.setOnClickListener(this)
        binding.tvShowChlDate.setOnClickListener(this)
        binding.tvShowChlTime.setOnClickListener(this)
        binding.btnChlColorPicker.setOnClickListener(this)
        binding.btnOpenSubChlSheet.setOnClickListener(this)

        binding.chlPaletteColor.setOnColorSelectedListener { clr ->
            chlColor = clr
            binding.btnChlColorPicker.setCardBackgroundColor(chlColor!!)
        }

        binding.chlRefresh.setOnRefreshListener {
            binding.chlRefresh.isRefreshing = false
            displaySubCheckList()
        }

    }

    private fun displaySubCheckList(){

        listOfSubChl = HelperDataBase.getDataBaseInstance(this).checkListDao().getAllSubCheckLists(checkLId) as ArrayList<SubCheckList>
        subChlAdapter = SubChlAdapter(this , listOfSubChl)
        binding.subChlRecyclerView.setHasFixedSize(true)
        subChlAdapter!!.notifyDataSetChanged()
        binding.subChlRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.subChlRecyclerView.adapter = subChlAdapter!!

    }

    private fun displayInfoFromAdapter(){
        if (checkLId != 0) {
            isComplete =  intent.getBooleanExtra("chlComplete" , false)
            binding.checkListTitle.setText(intent.getStringExtra("chlTitle"))
            binding.tvShowChlTime.text = intent.getStringExtra("chlTime")
            binding.tvShowChlDate.text = intent.getStringExtra("chlDate")
            chlColor = Integer.parseInt(intent.getStringExtra("chlColor")!!)
            binding.btnChlColorPicker.setCardBackgroundColor(chlColor!!)
            binding.chlPaletteColor.setSelectedColor(chlColor!!)

        }
    }
    private fun openTaskDateDialog() {

        val calender = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                calender.set(Calendar.YEAR, year)
                calender.set(Calendar.MONTH, month)
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormatted =
                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(calender.time)

                binding.tvShowChlDate.text = dateFormatted

            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    @SuppressLint("SimpleDateFormat")
    private fun openTaskTimeDialog() {
        val calender = Calendar.getInstance()
        val isSystem24Hour = android.text.format.DateFormat.is24HourFormat(this)
        val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calender.set(Calendar.MINUTE, minute)
            val timeFormatted = SimpleDateFormat("hh:mm a").format(calender.time)
            binding.tvShowChlTime.text = timeFormatted.toString()
        }
        TimePickerDialog(
            this, timePicker, calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE), isSystem24Hour
        ).show()
    }

    private fun openColorPicker() {
        if (showing) {
            binding.chlPaletteColor.visibility = View.GONE
            binding.hideColorPalette.visibility = View.GONE
            showing = false
        } else {
            binding.chlPaletteColor.visibility = View.VISIBLE
            binding.hideColorPalette.visibility = View.VISIBLE
            showing = true
        }
    }

    private fun backToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("ArrowKey", 2)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToHomeActivity()
    }

    private fun saveNewCheckList() {

        val title = binding.checkListTitle.text.toString()
        val time = binding.tvShowChlTime.text.toString()
        val date = binding.tvShowChlDate.text.toString()
        val checkListEntities = CheckListEntity(title, time, date, chlColor.toString(), isComplete)

        when (checkLId) {

            0 -> {
                if (title.isEmpty() || time.isEmpty() || date.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Error required filed is empty.. ${checkListEntities.checkList_Completed}!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    HelperDataBase.getDataBaseInstance(this).checkListDao()
                        .saveNewCheckList(checkListEntities)
                    backToHomeActivity()
                    Toast.makeText(
                        this,
                        "CheckList Saved. $${checkListEntities.checkList_Completed}!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            intent.getIntExtra("chlID", 0) -> {
                if (title == intent.getStringExtra("chlTitle") && time == intent.getStringExtra("chlTime") && date == intent.getStringExtra(
                        "chlDate")) {
                    Toast.makeText(
                        this,
                        "Sorry You Don't Update Anything In $title List !",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    checkListEntities.checkListId = checkLId
                    HelperDataBase.getDataBaseInstance(this).checkListDao()
                        .updateCurrentCheckList(checkListEntities)
                    backToHomeActivity()
                    Toast.makeText(
                        this,
                        "CheckList Updated. $title!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btn_arrowToHome -> {
                backToHomeActivity()
            }
            R.id.tv_ShowChlDate -> {
                openTaskDateDialog()
            }
            R.id.tv_ShowChlTime -> {
                openTaskTimeDialog()
            }
            R.id.btn_ChlColorPicker -> {
                openColorPicker()
            }
            R.id.btn_AddNewChl -> {
                saveNewCheckList()
            }
            R.id.btnOpenSubChlSheet -> {
               if (checkLId == 0){
                   Toast.makeText(this , "You need to create parent checklist first then can do this." , Toast.LENGTH_LONG).show()
               }else{
                   ChlBottomSheet(checkLId).show(supportFragmentManager, "Tag")
               }
            }
        }
    }


}