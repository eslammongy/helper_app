package com.eslammongy.helper.fragment.dialogs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.DeleteDialogLayoutBinding
import com.eslammongy.helper.ui.HomeActivity

class CustomDeleteDialog(itemDeletedID: Int) : Fragment() ,View.OnClickListener{

    private var _binding: DeleteDialogLayoutBinding? = null
    private val binding get() = _binding!!
    private var itemDeletedID: Int? = null
    private lateinit var endAnimation: Animation

    private lateinit var taskEntities: TaskEntities
    init {
        this.itemDeletedID = itemDeletedID
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

        endAnimation = AnimationUtils.loadAnimation(activity!!, R.anim.ending_animation)
        binding.btnExitDeleteDialog.setOnClickListener(this)
        binding.btnSetDeleteDialog.setOnClickListener(this)
        Toast.makeText(
            activity,
            "Error required filed is empty.. $itemDeletedID!",
            Toast.LENGTH_SHORT
        ).show()


    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.btn_ExitDeleteDialog -> {
                binding.deleteDialogLayout.visibility = View.GONE
                binding.deleteDialogLayout.startAnimation(endAnimation)

            }
            R.id.btn_SetDeleteDialog -> {
                taskEntities = TaskEntities()
                taskEntities.taskId = itemDeletedID!!
                if (itemDeletedID == 0) {
                    Toast.makeText(activity!! , "not found anything to delete it !!" , Toast.LENGTH_SHORT).show()

                }else{

                    HelperDataBase.getDataBaseInstance(activity!!).taskDao().deleteSelectedTask(taskEntities)
                    val intent = Intent(activity!! , HomeActivity::class.java)
                    startActivity(intent)
                    activity!!.finish()

                }

            }
        }
    }

}