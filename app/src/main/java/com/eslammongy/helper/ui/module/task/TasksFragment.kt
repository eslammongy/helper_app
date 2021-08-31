package com.eslammongy.helper.ui.module.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.FragmentTasksBinding
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.utilis.startSearchActivity
import com.eslammongy.helper.ui.baseui.BaseFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class TasksFragment : BaseFragment() , View.OnClickListener {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    var listMyTasks = ArrayList<TaskEntities>()
    private var taskAdapter: TaskAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch {
            withContext(Dispatchers.IO){
                listMyTasks =  HelperDataBase.getDataBaseInstance(requireActivity()).taskDao().getAllTasks() as ArrayList<TaskEntities>
            }
            if (listMyTasks.isEmpty()) {
                binding.emptyImageView.visibility = View.VISIBLE
            } else {
                binding.tasksRecyclerView.setHasFixedSize(true)
                taskAdapter = TaskAdapter(requireContext(), listMyTasks)
                binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.tasksRecyclerView.adapter = taskAdapter
            }
        }

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
                    val listTasks: TaskEntities = listMyTasks[position]
                    launch {
                        context?.let {
                            HelperDataBase.getDataBaseInstance(context!!).taskDao()
                                .deleteSelectedTask(listTasks)
                        }
                    }
                    deletedItem =
                        "Are You Sure You Want To Delete This " + listTasks.taskTitle + "OR Undo Deleted .."
                    listMyTasks.removeAt(viewHolder.adapterPosition)
                    taskAdapter!!.notifyDataSetChanged()
                    Snackbar.make(binding.tasksRecyclerView, deletedItem!!, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {

                            listMyTasks.add(position, listTasks)
                            launch {
                                HelperDataBase.getDataBaseInstance(requireContext()).taskDao()
                                    .saveNewTask(listTasks)
                            }
                            taskAdapter!!.notifyItemInserted(position)

                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerView)

        binding.btnAddNewElement.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_AddNewElement -> {
                launch {
                    requireActivity().startNewActivity(AddNewTask::class.java, 1)
                }

            }
            R.id.btn_Search -> {
                launch {
                    requireActivity().startSearchActivity(1)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}