package com.eslammongy.helper.ui.module.task_friends

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.FragmentTaskWithFriendAndSendEmailBinding
import com.eslammongy.helper.ui.module.task.TaskAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TaskWithFriendAndSendEmail (contactName: String, contactEmail: String , friendID:Int):DialogFragment() , CoroutineScope {
    private var _binding: FragmentTaskWithFriendAndSendEmailBinding? = null
    private val binding get() = _binding!!
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    private var listMyTasksWFriend = ArrayList<TaskEntities>()
    private var twfAdapter: TaskAdapter? = null
    private var contactName: String? = null
    private var contactEmail: String? = null
    private var friendID:Int = 0
    init {
        this.contactName = contactName
        this.contactEmail = contactEmail
        this.friendID = friendID
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskWithFriendAndSendEmailBinding.inflate(inflater, container, false)
        dialog!!.window!!.setWindowAnimations(R.style.AnimationDialog)
        job = Job()
        binding.tvShowFriendName.text = contactName
        launch {
        }
        if (contactEmail == "ShowingTaskList") {
            launch {
                displayTaskWithSelectedFriend()
            }
        } else {
            showSendingEmailForm()
        }

        binding.btnExitTWF.setOnClickListener {
           dialog!!.dismiss()
        }
        binding.btnSendEmail.setOnClickListener {
            sendingEmailMessage()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        val height = (resources.displayMetrics.heightPixels)
        val width = (resources.displayMetrics.widthPixels)
        sheetContainer.layoutParams.width = width
        sheetContainer.layoutParams.height = height
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sheetContainer.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    @SuppressLint("SetTextI18n")
    private suspend fun displayTaskWithSelectedFriend() {
        listMyTasksWFriend = HelperDataBase.getDataBaseInstance(requireContext()).contactDao().getTaskWithFriend(friendID) as ArrayList<TaskEntities>
        if (listMyTasksWFriend.isEmpty()) {
            binding.emptyListText.visibility = View.VISIBLE
            binding.emptyImageView.visibility = View.VISIBLE
            binding.emptyListText.text = "${resources.getString(R.string.you_didn_t_have_any_task)} $contactName"
            binding.twfRecyclerView.visibility = View.GONE
        } else {
            twfAdapter = TaskAdapter(requireContext(), listMyTasksWFriend)
            binding.twfRecyclerView.setHasFixedSize(true)
            binding.twfRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.twfRecyclerView.adapter = twfAdapter
        }
    }

    private fun showSendingEmailForm() {
        binding.SecondLayout.visibility = View.VISIBLE
        binding.twfRecyclerView.visibility = View.GONE
        binding.emptyListText.visibility = View.GONE
        binding.formEmailAddress.setText(contactEmail!!)

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun sendingEmailMessage() {

        val address = binding.formEmailAddress.text.toString()
        val subject = binding.formEmailSubject.text.toString()
        val message = binding.formEmailMessage.text.toString()

        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("mailto")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            type = "message/rfc822"
        }

        if (address.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(Intent.createChooser(intent, "Choose App"))
            } else {
                Toast.makeText(requireContext(), "Required Field Is Empty", Toast.LENGTH_LONG)
                    .show()
            }
        } else {

            Toast.makeText(requireContext(), "Required App Is Not Installed", Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}