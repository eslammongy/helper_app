package com.eslammongy.helper.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var binding:ActivitySearchBinding
    var listMyTasks = ArrayList<TaskEntities>()
    var taskAdapter: TaskAdapter? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.searchViewListener.isSubmitButtonEnabled = true
        binding.searchViewListener.setOnQueryTextListener(this)
    }

    fun backToHomeActivity(view: View) {

        val intent = Intent(this , HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
       if (query != null){
           getSelectedTask(query)
       }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            getSelectedTask(newText)
        }
        return true
    }
    private fun getSelectedTask(query:String){
        val searchQuery = "%$query%"
        listMyTasks = HelperDataBase.getDataBaseInstance(this).taskDao().searchInTaskDataBase(searchQuery) as ArrayList<TaskEntities>
        taskAdapter = TaskAdapter(this , listMyTasks)
        binding.searchRecyclerView.setHasFixedSize(true)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclerView.adapter = taskAdapter
    }
}