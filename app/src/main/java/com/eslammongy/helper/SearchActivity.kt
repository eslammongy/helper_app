package com.eslammongy.helper

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class SearchActivity : AppCompatActivity() {
    lateinit var testSearchKey:TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        testSearchKey = findViewById(R.id.testSearchKey)
        when(intent.getStringExtra("SearchKey")){

            "Task" ->{
                testSearchKey.text = "Search In Task DateBase"
            }
            "CheckList" ->{
                testSearchKey.text = "Search In CheckList DateBase"
            }
            "Contact" ->{
                testSearchKey.text = "Search In Contact DateBase"
            }
        }
    }

    fun backToHomeActivity(view: View) {

        val intent = Intent(this , HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}