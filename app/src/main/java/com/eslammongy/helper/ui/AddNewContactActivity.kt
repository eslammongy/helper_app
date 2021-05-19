package com.eslammongy.helper.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.ActivityAddNewContactBinding

class AddNewContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewContactBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_add_new_contact)
    }

    fun backToHomeActivity(view: View) {
        val intent = Intent(this , HomeActivity::class.java)
        intent.putExtra("ArrowKey" , 3)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this , HomeActivity::class.java)
        intent.putExtra("ArrowKey" , 3)
        startActivity(intent)
        finish()
    }

    fun openColorPickerDialog(view: View) {}
    fun saveNewContact(view: View) {}
    fun deleteCurrentContact(view: View) {}
    fun makePhoneCall(view: View) {}
    fun sendEmailMessage(view: View) {}
    fun shareMyContact(view: View) {}
    fun pickContactProfilePhoto(view: View) {}
}