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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.TaskAdapter
import com.eslammongy.helper.databinding.FragmentTaskWithFriendAndSendEmailBinding
import com.eslammongy.helper.viewModels.ContactViewMode

class TaskWithFriendAndSendEmail(contactName: String, contactEmail: String, friendID: Int) :
    DialogFragment() {
    private var _binding: FragmentTaskWithFriendAndSendEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var twfAdapter: TaskAdapter
    private var contactName: String? = null
    private var contactEmail: String? = null
    private var friendID: Int = 0
    private lateinit var contactViewMode: ContactViewMode

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

        contactViewMode = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(ContactViewMode::class.java)

        binding.tvShowFriendName.text = contactName
        if (contactEmail == "ShowingTaskList") {
            displayTaskWithSelectedFriend()
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
    private fun displayTaskWithSelectedFriend() {
        twfAdapter = TaskAdapter(requireContext())
        binding.twfRecyclerView.setHasFixedSize(true)
        binding.twfRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.twfRecyclerView.adapter = twfAdapter
      contactViewMode.getTasksWithFriend(friendID).observe(viewLifecycleOwner , {
          it.let { list ->
              if (list.isEmpty()) {
                  binding.emptyListText.visibility = View.VISIBLE
                  binding.emptyImageView.visibility = View.VISIBLE
                  binding.emptyListText.text =
                      "${resources.getString(R.string.you_didn_t_have_any_task)} $contactName"
                  binding.twfRecyclerView.visibility = View.GONE
              } else {
                 twfAdapter.differ.submitList(list)
              }
          }
      })

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

            Toast.makeText(requireContext(), "Required App Is Not Installed", Toast.LENGTH_LONG)
                .show()
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}