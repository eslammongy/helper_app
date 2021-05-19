package com.eslammongy.helper.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.ActivityAddNewCheckListBinding

class AddNewCheckListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewCheckListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCheckListBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_add_new_check_list)
    }


    fun openColorPickerDialog(view: View) {}

    fun backToHomeActivity(view: View) {
        val intent = Intent(this , HomeActivity::class.java)
        intent.putExtra("ArrowKey" , 2)
        startActivity(intent)
        finish()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this , HomeActivity::class.java)
        intent.putExtra("ArrowKey" , 2)
        startActivity(intent)
        finish()
    }
}