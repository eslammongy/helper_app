package com.eslammongy.helper.ui.dailogs

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.FragmentCustomDeleteDailogBinding
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.ui.baseui.BaseDialogFragment
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.viewModels.TaskViewModel
import kotlinx.coroutines.launch

class CustomDeleteDialog(itemDeletedID: Int , selectedDialog:Int)  : BaseDialogFragment() , View.OnClickListener{

    private var _binding: FragmentCustomDeleteDailogBinding? = null
    private val binding get() = _binding!!
    private var itemDeletedID: Int? = null
    private var selectedDialog:Int = 0
    private var contactEntities = ContactEntities()
    private var taskEntities = TaskEntities()
    private var checkListEntity = CheckListEntity()
    private lateinit var taskViewModel: TaskViewModel

    init {
        this.itemDeletedID = itemDeletedID
        this.selectedDialog = selectedDialog
    }
    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentCustomDeleteDailogBinding.inflate(inflater, container, false)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationDialog)
        taskViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(TaskViewModel::class.java)
        binding.btnExitDeleteDialog.setOnClickListener(this)
        binding.btnSetDeleteDialog.setOnClickListener(this)
        setDeleteDialogText(selectedDialog)
        binding.btnExitDeleteDialog.setOnClickListener(this)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setDeleteDialogText(selectText:Int){
        when(selectText){
            1 ->{
                binding.deleteDialogText.text = "Are You Sure You Want To Delete This Task OR Undo Deleted It .."
            }
            2 ->{
                binding.deleteDialogText.text = "Note that this action will delete all the SubLists that the list contains.If you think this to-do list is over.Let's delete it to devote time to other tasks. Let's get it over with and get some rest."
            }
            3 ->{
                binding.deleteDialogText.text = "Are You Sure You Want To Delete This Friend OR Undo Deleted It .."

            }
        }

    }
    private fun setDeleteDialogFun(itemID:Int , dialogID:Int){
        taskEntities.taskId = itemID
        contactEntities.contactId = itemID
        checkListEntity.checkListId = itemID
        when(dialogID){
            1 ->{

                taskViewModel.deleteSelectedTask(taskEntities)
                requireActivity().startNewActivity(HomeScreen::class.java , 1)
            }
            2 ->{
                launch {
                    HelperDataBase.getDataBaseInstance(requireContext()).checkListDao().deleteSelectedCheckList(checkListEntity)
                }
                requireActivity().startNewActivity(HomeScreen::class.java , 2)
            }
            3 ->{
                launch {
                    HelperDataBase.getDataBaseInstance(requireContext()).contactDao().deleteSelectedContact(contactEntities)
                }
                requireActivity().startNewActivity(HomeScreen::class.java , 3)

            }
        }

    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        sheetContainer.layoutParams.width = width
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sheetContainer.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btn_ExitDeleteDialog -> {
               dialog!!.dismiss()
            }
            R.id.btn_SetDeleteDialog -> {
                setDeleteDialogFun(itemDeletedID!!, selectedDialog)
            }
        }
    }
}