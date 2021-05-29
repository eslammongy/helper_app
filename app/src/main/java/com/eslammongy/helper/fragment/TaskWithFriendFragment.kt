package com.eslammongy.helper.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.FragmentTaskWithFriendBinding
import java.util.*

@SuppressLint("SetTextI18n")
class TaskWithFriendFragment(contactName: String, displayOption: String) : Fragment() {

    private var _binding: FragmentTaskWithFriendBinding? = null
    private val binding get() = _binding!!
    private var listMyTasksWFriend = ArrayList<TaskEntities>()
    private var twfAdapter: TaskAdapter? = null
    private var contactName: String? = null
    private var displayOption: String? = null
    private lateinit var endAnimation: Animation

    init {
        this.contactName = contactName
        this.displayOption = displayOption
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskWithFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        endAnimation = AnimationUtils.loadAnimation(activity!!, R.anim.ending_animation)
        binding.tvShowFriendName.text = contactName
        listMyTasksWFriend = HelperDataBase.getDataBaseInstance(activity!!).taskDao()
            .getTaskWithFriend(contactName!!) as ArrayList<TaskEntities>

        if (displayOption == "ShowTask") {
            displayTaskWithSelectedFriend()

        } else {

            showSendingEmailForm()
        }

        binding.btnExitTWF.setOnClickListener {
            fragmentManager!!.beginTransaction().remove(TaskWithFriendFragment("none", "Noun"))
                .commitAllowingStateLoss()
            binding.parentView.visibility = View.GONE
            binding.parentView.startAnimation(endAnimation)
        }
        binding.btnSendEmail.setOnClickListener {
            sendingEmailMessage()
        }
    }

    private fun displayTaskWithSelectedFriend() {
        if (listMyTasksWFriend.isEmpty()) {
            binding.emptyListText.visibility = View.VISIBLE
            binding.emptyListText.text = "${R.string.you_didn_t_have_any_task}$contactName"
            binding.twfRecyclerView.visibility = View.GONE
        } else {
            binding.emptyListText.visibility = View.GONE
            twfAdapter = TaskAdapter(context!!, listMyTasksWFriend)
            binding.twfRecyclerView.setHasFixedSize(true)
            binding.twfRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.twfRecyclerView.adapter = twfAdapter
        }
    }

    private fun showSendingEmailForm() {
        binding.SecondLayout.visibility = View.VISIBLE
        binding.twfRecyclerView.visibility = View.GONE
        binding.emptyListText.visibility = View.GONE
        binding.formEmailAddress.setText(displayOption!!)

    }

    private fun sendingEmailMessage() {

        val address = binding.formEmailAddress.text.toString()
        val subject = binding.formEmailSubject.text.toString()
        val message = binding.formEmailMessage.text.toString()

        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto")
            putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(address))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            type = "message/rfc822"
        }

        if (address.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            if (intent.resolveActivity(context!!.packageManager) != null) {
                startActivity(Intent.createChooser(intent, "Choose App"))
            } else {
                Toast.makeText(activity!!, "Required Field Is Empty", Toast.LENGTH_LONG)
                    .show()
            }
        } else {

            Toast.makeText(activity!!, "Required App Is Not Installed", Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}