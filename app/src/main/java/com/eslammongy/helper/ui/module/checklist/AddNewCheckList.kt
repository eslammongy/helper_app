package com.eslammongy.helper.ui.module.checklist

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.eslammongy.helper.R
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.databinding.ActivityAddNewCheckListBinding
import com.eslammongy.helper.ui.dailogs.CustomDeleteDialog
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.ui.module.sublist.ChlBottomSheet
import com.eslammongy.helper.ui.module.sublist.SubChlFragment
import com.eslammongy.helper.utilis.setToastMessage
import com.eslammongy.helper.utilis.showingSnackBar
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.viewModels.ChListViewModel
import java.text.DateFormat
import java.util.*

class AddNewCheckList : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddNewCheckListBinding
    private var showing = false
    private var chlColor: Int? = null
    private var checkLId: Int = 0
    private var isComplete: Boolean = false
    private lateinit var chListViewModel: ChListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCheckListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chListViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(ChListViewModel::class.java)
        checkLId = intent.getIntExtra("chlID", 0)
        isComplete = intent.getBooleanExtra("chlComplete", false)
        chlColor = ResourcesCompat.getColor(resources, R.color.ColorDefaultNote, theme)
        if (checkLId != 0){
            displayInfoFromAdapter()
            binding.parentView.setBackgroundColor(chlColor!!)
        }
        replaceFragment(checkLId)
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

    private fun displayInfoFromAdapter() {

            binding.checkListTitle.setText(intent.getStringExtra("chlTitle"))
            binding.tvShowChlDate.text = intent.getStringExtra("chlDate")
            chlColor = Integer.parseInt(intent.getStringExtra("chlColor")!!)
            binding.btnChlColorPicker.setCardBackgroundColor(chlColor!!)
            binding.chlPaletteColor.setSelectedColor(chlColor!!)
            binding.btnDeleteChl.visibility = View.VISIBLE
            binding.btnOpenSubChlSheet.visibility = View.VISIBLE
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

    private fun replaceFragment(subChlId: Int) {
        val subChlFragment = SubChlFragment(subChlId, binding.checkListTitle.text.toString())
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.subChlFragmentContainer, subChlFragment)
            .commit()
    }

    private fun saveNewCheckList() {
        val id = checkLId
        val title = binding.checkListTitle.text.toString()
        val date = binding.tvShowChlDate.text.toString()
        val checkListEntities = CheckListEntity(title, date, chlColor.toString(), isComplete)
        when (id) {
            0 -> {
                if (title.isEmpty() || date.isEmpty()) {
                    showingSnackBar(
                        binding.root,
                        "Please make sure all fields are filled",
                        "#F98404"
                    )
                } else {
                    chListViewModel.saveNewChLIst(checkListEntities)
                    setToastMessage("CheckList Saved.",  Color.parseColor("#6ECB63"))
                    this.startNewActivity(HomeScreen::class.java, 2)

                }
            }
            intent.getIntExtra("chlID", 0) -> {
                if (title == intent.getStringExtra("chlTitle") && date == intent.getStringExtra("chlDate")) {
                    showingSnackBar(
                        binding.root,
                        "Please make sure you've updated anything.",
                        "#F98404"
                    )
                } else {
                    checkListEntities.checkListId = checkLId
                    checkListEntities.checkList_Completed = isComplete
                    chListViewModel.updateCurrentChList(checkListEntities)
                    setToastMessage("CheckList Updated", Color.parseColor("#6ECB63"))
                    this.startNewActivity(HomeScreen::class.java, 2)

                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.startNewActivity(HomeScreen::class.java, 2)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btn_arrowToHome -> {
                this.startNewActivity(HomeScreen::class.java, 2)
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
                    ChlBottomSheet(checkLId, binding.checkListTitle.text.toString()).show(
                        supportFragmentManager,
                        "Tag")
            }
            R.id.btnDeleteChl -> {
                CustomDeleteDialog(checkLId, 2).show(supportFragmentManager, "TAG")

            }

        }
    }
}