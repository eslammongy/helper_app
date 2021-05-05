package com.eslammongy.helper

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.eslammongy.helper.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySearchBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_search)

        when(intent.getStringExtra("SearchKey")){

            "Task" ->{
                binding.testSearchKey.text = "Search In Task DateBase"
            }
            "CheckList" ->{
                binding.testSearchKey.text = "Search In CheckList DateBase"
            }
            "Contact" ->{
                binding.testSearchKey.text = "Search In Contact DateBase"
            }
        }
    }

    fun backToHomeActivity(view: View) {

        val intent = Intent(this , HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}