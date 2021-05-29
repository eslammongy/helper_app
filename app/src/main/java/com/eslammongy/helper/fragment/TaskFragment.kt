package com.eslammongy.helper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.FragmentTaskBinding
import com.google.android.material.snackbar.Snackbar

class TaskFragment : Fragment(){

    var listMyTasks = ArrayList<TaskEntities>()
    var taskAdapter:TaskAdapter? = null
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      displayRecyclerView()
        var deletedItem:String?
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position:Int = viewHolder.adapterPosition
                    val listTasks:TaskEntities = listMyTasks[position]
                    HelperDataBase.getDataBaseInstance(activity!!).taskDao().deleteSelectedTask(listTasks)
                    deletedItem = "Are You Sure You Want To Delete This "+ listTasks.taskTitle + "OR Undo Deleted .."
                    listMyTasks.removeAt(viewHolder.adapterPosition)
                    taskAdapter!!.notifyDataSetChanged()
                    Snackbar.make(binding.tasksRecyclerView , deletedItem!! , Snackbar.LENGTH_LONG).setAction("Undo"
                    ) {

                        listMyTasks.add(position, listTasks)
                        HelperDataBase.getDataBaseInstance(activity!!).taskDao()
                            .saveNewTask(listTasks)
                        taskAdapter!!.notifyItemInserted(position)

                    }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerView)


}

    private fun displayRecyclerView(){
        listMyTasks = HelperDataBase.getDataBaseInstance(activity!!).taskDao().getAllTasks() as ArrayList<TaskEntities>
        taskAdapter = TaskAdapter(context!! , listMyTasks)
        binding.tasksRecyclerView.setHasFixedSize(true)
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context )
        binding.tasksRecyclerView.adapter = taskAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

