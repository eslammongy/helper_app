package com.eslammongy.helper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.FragmentTaskBinding

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

        listMyTasks = HelperDataBase.getDataBaseInstance(activity!!).taskDao().getAllTasks() as ArrayList<TaskEntities>
        taskAdapter = TaskAdapter(context!! , listMyTasks)
        binding.tasksRecyclerView.setHasFixedSize(true)
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(context )
        binding.tasksRecyclerView.adapter = taskAdapter
    }


}

