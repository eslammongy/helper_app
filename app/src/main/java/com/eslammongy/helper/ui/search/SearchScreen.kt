package com.eslammongy.helper.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.CheckListAdapter
import com.eslammongy.helper.adapters.ContactAdapter
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.databinding.ActivitySearchScreenBinding
import com.eslammongy.helper.ui.home.HomeScreen
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.viewModels.ChListViewModel
import com.eslammongy.helper.viewModels.ContactViewMode
import com.eslammongy.helper.viewModels.TaskViewModel

class SearchScreen : AppCompatActivity(){

    private lateinit var binding: ActivitySearchScreenBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var chListViewModel: ChListViewModel
    private lateinit var contactViewMode: ContactViewMode
    private val taskAdapter by lazy { TaskAdapter(this) }
    private val contactAdapter by lazy {ContactAdapter (this)}
    private val chlAdapter by lazy { CheckListAdapter(this) }
    private var searchID: Int = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(TaskViewModel::class.java)

        chListViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(ChListViewModel::class.java)

        contactViewMode = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(ContactViewMode::class.java)
        searchID = intent.getIntExtra("SearchID", 0)
        setSearchBarHint(searchID)
        binding.btnBackToHomeMT.setOnClickListener {
            startNewActivity(HomeScreen::class.java , searchID)
        }

        binding.searchRecyclerView.setHasFixedSize(true)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)

       binding.searchViewListener.addTextChangedListener(object :TextWatcher{
           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

           }
           override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

           }
           override fun afterTextChanged(text: Editable?) {
               val newText = text.toString()
               when(searchID){
                   1 ->{
                       getSelectedTask(newText)
                   }
                   2 ->{
                       getSelectedChList(newText)
                   }
                   3->{
                       getSelectedContact(newText)
                   }
               }
           }
       })


    }

    private fun setSearchBarHint(searchID:Int){
        when(searchID){
            1 ->{
                binding.searchViewListener.hint = "search in your tasks.."
            }
            2 ->{
                binding.searchViewListener.hint = "search in your check lists.."
            }
            3 ->{
                binding.searchViewListener.hint = "search in your contacts.."
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

    private fun getSelectedChList(query:String){
        val searchQuery =  "%$query%"
        binding.searchRecyclerView.adapter = chlAdapter
        chListViewModel.searchInChListDatabase(searchQuery).observe(this , {chList->
            chList.let {
                chlAdapter.differ.submitList(it)
            }
        })

    }

    private fun getSelectedContact(query: String) {
        val searchQuery =  "%$query%"
        binding.searchRecyclerView.adapter = contactAdapter
        contactViewMode.searchInContactDataBase(searchQuery).observe(this , { list->
            list.let {
                contactAdapter.differ.submitList(it)
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startNewActivity(HomeScreen::class.java, searchID)
    }

}
