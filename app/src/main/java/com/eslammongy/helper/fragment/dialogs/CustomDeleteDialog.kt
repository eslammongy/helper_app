package com.eslammongy.helper.fragment.dialogs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.DeleteDialogLayoutBinding
import com.eslammongy.helper.ui.HomeActivity

class CustomDeleteDialog(itemDeletedID: Int , selectedDialog:Int) : Fragment() ,View.OnClickListener{

    private var _binding: DeleteDialogLayoutBinding? = null
    private val binding get() = _binding!!
    private var itemDeletedID: Int? = null
    private var selectedDialog:Int = 0
    private lateinit var startAnimation: Animation
    private lateinit var endAnimation: Animation
    private var contactEntities = ContactEntities()
    private var taskEntities = TaskEntities()
    private var checkListEntity = CheckListEntity()
    init {
        this.itemDeletedID = itemDeletedID
        this.selectedDialog = selectedDialog
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View{
        _binding = DeleteDialogLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.starting_animation)
        endAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.ending_animation)
        binding.deleteDialogLayout.startAnimation(startAnimation)
        setDeleteDialogText(selectedDialog)
        binding.btnExitDeleteDialog.setOnClickListener(this)
        binding.btnSetDeleteDialog.setOnClickListener(this)
       // Toast.makeText(activity, "Error required filed is empty.. $itemDeletedID!", Toast.LENGTH_SHORT).show()


    }

    @SuppressLint("SetTextI18n")
    private fun setDeleteDialogText(selectText:Int){
        when(selectText){
            1 ->{
                binding.deleteDialogText.text = "Are You Sure You Want To Delete This Task OR Undo Deleted It .."
            }
            2 ->{
                binding.deleteDialogText.text = "Are You Sure You Want To Delete This Friend OR Undo Deleted It .."
            }
            3 ->{
                binding.deleteDialogText.text = "If you think this to-do list is over. Delete it to devote time to other tasks. Let's get it done. and take a rest"
            }
        }

    }

    private fun setDeleteDialogFun(itemID:Int , dialogID:Int){
        taskEntities.taskId = itemID
        contactEntities.contactId = itemID
        checkListEntity.checkListId = itemID
        when(dialogID){
            1 ->{
                HelperDataBase.getDataBaseInstance(requireContext()).taskDao().deleteSelectedTask(taskEntities)
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            2 ->{
                HelperDataBase.getDataBaseInstance(requireContext()).contactDao().deleteSelectedContact(contactEntities)
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            3 ->{
                HelperDataBase.getDataBaseInstance(requireContext()).checkListDao().deleteSelectedCheckList(checkListEntity)
                binding.parentView.visibility = View.GONE
                binding.parentView.startAnimation(endAnimation)

            }
        }

    }
    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.btn_ExitDeleteDialog -> {
                binding.parentView.visibility = View.GONE
                binding.parentView.startAnimation(endAnimation)

            }
            R.id.btn_SetDeleteDialog -> {
                setDeleteDialogFun(itemDeletedID!!, selectedDialog)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}