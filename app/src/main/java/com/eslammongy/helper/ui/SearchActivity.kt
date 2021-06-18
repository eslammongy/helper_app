package com.eslammongy.helper.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.CheckListAdapter
import com.eslammongy.helper.adapters.ContactAdapter
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

    private lateinit var binding:ActivitySearchBinding
    private var listMyTasks = ArrayList<TaskEntities>()
    private var taskAdapter: TaskAdapter? = null
    private var listMyContact = ArrayList<ContactEntities>()
    private var contactAdapter: ContactAdapter? = null
    private var listMyChl = ArrayList<CheckListEntity>()
    private var chlAdapter: CheckListAdapter? = null
    private var searchID:Int = 0
    private var arrowKey:Int = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchID = intent.getIntExtra("SearchID" , 0)
        when(intent.getStringExtra("SearchKey")){

            "Task" ->{
                binding.testSearchKey.text = "Search In Task DateBase"
                arrowKey = 1
            }
            "CheckList" ->{
                binding.testSearchKey.text = "Search In CheckList DateBase"
                arrowKey = 2
            }
            "Contact" ->{
                binding.testSearchKey.text = "Search In Contact DateBase"
                arrowKey = 3
            }
        }
        binding.searchViewListener.isSubmitButtonEnabled = true
        binding.searchViewListener.setOnQueryTextListener(this)
    }

    fun backToHomeActivity(view: View) {

        val intent = Intent(this , HomeActivity::class.java)
        intent.putExtra("ArrowKey", arrowKey)
        startActivity(intent)
        finish()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
       if (query != null){
           getSelectedTask(query , searchID)
       }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            getSelectedTask(newText , searchID)
        }
        return true
    }
    private fun getSelectedTask(query:String , searchId:Int){
        val searchQuery = "%$query%"

        when(searchId){

            1->{
                listMyTasks = HelperDataBase.getDataBaseInstance(this).taskDao().searchInTaskDataBase(searchQuery) as ArrayList<TaskEntities>
                taskAdapter = TaskAdapter(this , listMyTasks)
                binding.searchRecyclerView.setHasFixedSize(true)
                binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.searchRecyclerView.adapter = taskAdapter
                binding.testSearchKey.visibility = View.GONE
            }
            2->{
                listMyContact = HelperDataBase.getDataBaseInstance(this).contactDao().searchInContactDataBase(searchQuery) as ArrayList<ContactEntities>
                contactAdapter = ContactAdapter(this , listMyContact)
                binding.searchRecyclerView.setHasFixedSize(true)
                binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.searchRecyclerView.adapter = contactAdapter
                binding.testSearchKey.visibility = View.GONE

            }
            3->{
                listMyChl = HelperDataBase.getDataBaseInstance(this).checkListDao().searchInCheckListDataBase(searchQuery) as ArrayList<CheckListEntity>
                chlAdapter = CheckListAdapter(this)
                binding.searchRecyclerView.setHasFixedSize(true)
                binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.searchRecyclerView.adapter = chlAdapter
                chlAdapter!!.setData(listMyChl)
                binding.testSearchKey.visibility = View.GONE

            }
        }

    }

}