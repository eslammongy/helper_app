package com.eslammongy.helper.ui.module.search

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.ActivitySearchScreenBinding
import com.eslammongy.helper.helpers.startNewActivity
import com.eslammongy.helper.ui.module.checklist.CheckListAdapter
import com.eslammongy.helper.ui.module.contact.ContactAdapter
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.ui.module.task.TaskAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchScreen : AppCompatActivity(), CoroutineScope,
   SearchView.OnQueryTextListener {
    private lateinit var binding: ActivitySearchScreenBinding
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    private var listMyTasks = ArrayList<TaskEntities>()
    private var taskAdapter = TaskAdapter(this, listMyTasks)
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

        job = Job()
        searchID = intent.getIntExtra("SearchID", 0)
        binding.btnBackToHomeMT.setOnClickListener {
            startNewActivity(HomeScreen::class.java , searchID)
        }
        binding.searchViewListener.setOnQueryTextListener(this)
        binding.searchViewListener.isSubmitButtonEnabled = true


    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            launch {
                getSelectedTask(query, searchID)
            }
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            launch {
                getSelectedTask(newText, searchID)
            }
        }
        return true
    }

    private suspend fun getSelectedTask(query:String, searchId:Int){
        val searchQuery =  "%$query%"
        binding.searchRecyclerView.setHasFixedSize(true)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        when(searchId){

            1->{
                listMyTasks = HelperDataBase.getDataBaseInstance(this).taskDao().searchInTaskDataBase(searchQuery) as ArrayList<TaskEntities>
                taskAdapter = TaskAdapter(this , listMyTasks)
                binding.searchRecyclerView.adapter = taskAdapter
            }
            2->{
                listMyChl = HelperDataBase.getDataBaseInstance(this).checkListDao().searchInCheckListDataBase(searchQuery) as ArrayList<CheckListEntity>
                binding.searchRecyclerView.adapter = chlAdapter
                chlAdapter.setData(listMyChl)
            }
            3->{
                listMyContact = HelperDataBase.getDataBaseInstance(this).contactDao().searchInContactDataBase(searchQuery) as ArrayList<ContactEntities>
                contactAdapter = ContactAdapter(this , listMyContact)
                binding.searchRecyclerView.adapter = contactAdapter
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startNewActivity(HomeScreen::class.java, searchID)
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}