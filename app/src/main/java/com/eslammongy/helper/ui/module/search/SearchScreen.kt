package com.eslammongy.helper.ui.module.search

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.CheckListAdapter
import com.eslammongy.helper.adapters.ContactAdapter
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.ActivitySearchScreenBinding
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.utilis.setToastMessage
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.viewModels.TaskViewModel
import kotlinx.coroutines.launch

class SearchScreen : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding: ActivitySearchScreenBinding
    private lateinit var taskViewModel: TaskViewModel
    private val taskAdapter by lazy { TaskAdapter(this) }
    private var listMyContact = ArrayList<ContactEntities>()
    private var contactAdapter = ContactAdapter(this, listMyContact)
    private var listMyChl = ArrayList<CheckListEntity>()
    private var chlAdapter = CheckListAdapter(this)
    private var searchID: Int = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(TaskViewModel::class.java)
        searchID = intent.getIntExtra("SearchID", 0)
        setSearchBarHint(searchID)
        binding.btnBackToHomeMT.setOnClickListener {
            startNewActivity(HomeScreen::class.java , searchID)
        }

        binding.searchRecyclerView.setHasFixedSize(true)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.searchViewListener.setOnQueryTextListener(this)
        binding.searchViewListener.isSubmitButtonEnabled = true

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            when(searchID){
                1 ->{
                    getSelectedTask(newText)
                }
            }
        }
        return true
    }

    private fun setSearchBarHint(searchID:Int){
        when(searchID){
            1 ->{
                binding.searchViewListener.queryHint = "search in your tasks.."
            }
            2 ->{
                binding.searchViewListener.queryHint = "search in your check lists.."
            }
            3 ->{
                binding.searchViewListener.queryHint = "search in your contacts.."
            }
        }
    }

    private fun getSelectedTask(query:String){
        val searchQuery =  "%$query%"
        binding.searchRecyclerView.adapter = taskAdapter
        taskViewModel.searchInTaskDatabase(searchQuery).observe(this , {taskList->
            taskList.let {
                taskAdapter.differ.submitList(it)
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startNewActivity(HomeScreen::class.java, searchID)
    }

}
