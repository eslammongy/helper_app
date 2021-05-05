package com.eslammongy.helper.fragment.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.eslammongy.helper.AddNewTaskActivity
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.FragmentCustomDialogBinding

class CustomDialog() : DialogFragment() ,View.OnClickListener{

    private var taskColor: Int? = null
    private var _binding: FragmentCustomDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener:SetDate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View{
        _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)
       // dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation_Window

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.setContentView(R.layout.fragment_custom_dialog)
        dialog!!.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation_Window
         dialog!!.window!!.attributes.windowAnimations = R.anim.ending_animation
        binding.btnExitColorDialog.setOnClickListener(this)
        binding.btnSaveColor.setOnClickListener(this)

    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation_Window

    }
    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.btnExitColorDialog -> {
                dialog!!.window?.attributes!!.windowAnimations = R.style.DialogAnimation_Window
                dismiss()

            }
            R.id.btnSaveColor -> {
                val selectedColor = this.taskColor
                listener.getMessage(selectedColor.toString())
                dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation_Window

                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SetDate
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement ExampleDialogListener"
            )
        }
    }

    interface SetDate{
        fun getMessage(message: String)
    }


}