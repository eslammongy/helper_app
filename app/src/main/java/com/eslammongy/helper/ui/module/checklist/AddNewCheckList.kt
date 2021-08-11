package com.eslammongy.helper.ui.module.checklist

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.databinding.ActivityAddNewCheckListBinding
import com.eslammongy.helper.helpers.showingSnackBar
import com.eslammongy.helper.helpers.startNewActivity
import com.eslammongy.helper.ui.dailogs.CustomDeleteDialog
import com.eslammongy.helper.ui.module.sublist.ChlBottomSheet
import com.eslammongy.helper.ui.module.sublist.SubChlFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class AddNewCheckList : AppCompatActivity(), View.OnClickListener, CoroutineScope {
    private  lateinit var binding: ActivityAddNewCheckListBinding
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    private var showing = false
    private var chlColor: Int? = null
    private var checkLId: Int = 0
    private var isComplete:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCheckListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()
        checkLId = intent.getIntExtra("chlID", 0)
        chlColor = ResourcesCompat.getColor(resources, R.color.ColorDefaultNote, theme)
        launch {
            displayInfoFromAdapter()
            replaceFragment(checkLId )

        }
        binding.chlPaletteColor.setOnColorSelectedListener { clr ->
            chlColor = clr
            binding.btnChlColorPicker.setCardBackgroundColor(chlColor!!)
        }
        binding.btnArrowToHome.setOnClickListener(this)
        binding.btnAddNewChl.setOnClickListener(this)
        binding.setChlCalender.setOnClickListener(this)
        binding.btnChlColorPicker.setOnClickListener(this)
        binding.btnOpenSubChlSheet.setOnClickListener(this)
        binding.btnDeleteChl.setOnClickListener(this)

    }

    private fun displayInfoFromAdapter(){
        if (checkLId != 0) {
            isComplete =  intent.getBooleanExtra("chlComplete" , false)
            binding.checkListTitle.setText(intent.getStringExtra("chlTitle"))
            //binding.tvShowChlTime.text = intent.getStringExtra("chlTime")
            binding.tvShowChlDate.text = intent.getStringExtra("chlDate")
            chlColor = Integer.parseInt(intent.getStringExtra("chlColor")!!)
            binding.btnChlColorPicker.setCardBackgroundColor(chlColor!!)
            binding.chlPaletteColor.setSelectedColor(chlColor!!)
            binding.btnDeleteChl.visibility = View.VISIBLE
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun setChlDateTime() {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                this@AddNewCheckList,
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
//                    TimePickerDialog(
//                        this@AddNewCheckList,
//                        0,
//                        { _, hour, minute ->
//                            this.set(Calendar.HOUR_OF_DAY, hour)
//                            this.set(Calendar.MINUTE, minute)
//                            val timeFormatted = SimpleDateFormat("hh:mm a").format(this.time)
//                            binding.tvShowChlTime.text = timeFormatted.toString()
//                        },
//                        this.get(Calendar.HOUR_OF_DAY),
//                        this.get(Calendar.MINUTE),
//                        false
//                    ).show()
                    val dateFormatted =
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(this.time)
                    binding.tvShowChlDate.text = dateFormatted.toString()

                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
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

    private fun replaceFragment(subChlId:Int ){
        val subChlFragment = SubChlFragment(subChlId , binding.checkListTitle.text.toString())
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.subChlFragmentContainer, subChlFragment)
            .commit()
    }

    private fun saveNewCheckList() {
        val title = binding.checkListTitle.text.toString()
        //val time = binding.tvShowChlTime.text.toString()
        val date = binding.tvShowChlDate.text.toString()
        val checkListEntities = CheckListEntity(title, date, chlColor.toString(), isComplete)
        when (checkLId) {

            0 -> {
                if (title.isEmpty() || date.isEmpty()) {
                    showingSnackBar(binding.root , "Error required filed is empty." , "#FF5722")
                } else {
                    launch {
                        HelperDataBase.getDataBaseInstance(this@AddNewCheckList).checkListDao()
                            .saveNewCheckList(checkListEntities)
                    }
                    this.startNewActivity(HomeScreen::class.java , 2)

                }
            }
            intent.getIntExtra("chlID", 0) -> {
                if (title == intent.getStringExtra("chlTitle") && date == intent.getStringExtra("chlDate") ) {
                    showingSnackBar(binding.root , "Sorry You Don't Update Anything !!" , "#FF5722")
                } else {
                    checkListEntities.checkListId = checkLId
                    checkListEntities.checkList_Completed = isComplete
                    launch {
                        HelperDataBase.getDataBaseInstance(this@AddNewCheckList).checkListDao()
                            .updateCurrentCheckList(checkListEntities)
                    }

                    this.startNewActivity(HomeScreen::class.java , 2)

                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
       this.startNewActivity(HomeScreen::class.java , 2)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btn_arrowToHome -> {
                this.startNewActivity(HomeScreen::class.java , 2)
            }
            R.id.setChlCalender -> {
                setChlDateTime()
            }
            R.id.btn_ChlColorPicker -> {
                openColorPicker()
            }
            R.id.btn_AddNewChl -> {
                saveNewCheckList()
            }
            R.id.btnOpenSubChlSheet -> {
                if (checkLId == 0) {
                    showingSnackBar(binding.root , "first you need to include parent check list" , "#FF5722")
                } else {
                    ChlBottomSheet(checkLId , binding.checkListTitle.text.toString()).show(supportFragmentManager, "Tag")
                }
            }
            R.id.btnDeleteChl ->{
                launch {
                   CustomDeleteDialog(checkLId , 2).show(supportFragmentManager , "TAG")
                }
            }

        }
    }
}