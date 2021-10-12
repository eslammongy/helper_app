package com.eslammongy.helper.ui.module.task

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.FragmentTasksBinding
import com.eslammongy.helper.ui.baseui.BaseFragment
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.utilis.startSearchActivity
import com.eslammongy.helper.viewModels.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class TasksFragment : BaseFragment() , View.OnClickListener {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var imm:InputMethodManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(TaskViewModel::class.java)
        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        displayTasksRecyclerView()
        var deletedItem: String?
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false

                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position: Int = viewHolder.adapterPosition
                    val listTasks: TaskEntities = taskAdapter.differ.currentList[position]
                    deletedItem =
                        "Are You Sure You Want To Delete This OR Undo Deleted .."
                    taskViewModel.deleteSelectedTask(listTasks)
                    taskAdapter.notifyDataSetChanged()
                    Snackbar.make(binding.tasksRecyclerView, deletedItem!!, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {
                            taskViewModel.saveNewTask(listTasks)
                            taskAdapter.notifyItemInserted(position)
                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerView)

        binding.btnAddNewElement.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
    }

    private fun displayTasksRecyclerView(){
        taskAdapter = TaskAdapter(requireActivity())
        binding.tasksRecyclerView.apply {
            binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.tasksRecyclerView.setHasFixedSize(true)
            adapter = taskAdapter
        }
            taskViewModel.allTasksList.observe(viewLifecycleOwner , {
                if (it.isEmpty()){
                    binding.emptyImageView.visibility = View.VISIBLE
                    taskAdapter.differ.submitList(it)
                }else{
                    binding.emptyImageView.visibility = View.GONE
                    taskAdapter.differ.submitList(it)
                }
            })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_AddNewElement -> {
                launch {
                    requireActivity().startNewActivity(AddNewTask::class.java, 1)
                }
            }
            R.id.btn_Search -> {
                requireActivity().startSearchActivity(1)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}