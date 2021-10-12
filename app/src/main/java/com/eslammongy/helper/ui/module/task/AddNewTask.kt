package com.eslammongy.helper.ui.module.task

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eslammongy.helper.R
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.ActivityAddNewTaskBinding
import com.eslammongy.helper.services.AlarmService
import com.eslammongy.helper.ui.dailogs.CustomDeleteDialog
import com.eslammongy.helper.ui.dailogs.CustomWebView
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.ui.module.task_friends.TaskBottomSheet
import com.eslammongy.helper.utilis.*
import com.eslammongy.helper.viewModels.TaskViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*

class AddNewTask : AppCompatActivity(), View.OnClickListener, TaskBottomSheet.BottomSheetInterface {
    private lateinit var binding: ActivityAddNewTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private var taskColor: Int = 0
    private var taskID: Int = 0
    private var notifyTask: Int = 0
    private var friendID: Int = 0
    private var taskAlarm: Long = 0L
    private var imageFilePath: String = "ImagePath"
    private lateinit var alarmService: AlarmService
    private val userPermission by lazy { UserPermission(this) }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(TaskViewModel::class.java)
        alarmService = AlarmService(this)
        taskID = intent.getIntExtra("ID", 0)
        notifyTask = intent.getIntExtra("TaskNotifiedID", 0)
        binding.btnBackToHomeMT.setOnClickListener(this)
        binding.btnOpenBottomSheet.setOnClickListener(this)
        binding.btnDeleteTask.setOnClickListener(this)
        binding.btnOpenMyGallery.setOnClickListener(this)
        binding.btnSaveTask.setOnClickListener(this)
        binding.tvShowTaskLink.setOnClickListener(this)
        selectAndCompressImage()
        binding.bottomView.setBackgroundColor(
            ResourcesCompat.getColor(resources, R.color.ColorDefaultNote, theme)
        )
        if (taskID != 0) {

            displayTaskInfo(
                intent.getStringExtra("Title")!!,
                intent.getStringExtra("Content")!!,
                intent.getStringExtra("Date")!!,
                intent.getStringExtra("Time")!!,
                intent.getStringExtra("Link")!!,
                intent.getStringExtra("TaskImage")!!,
                intent.getStringExtra("Color")!!.toInt(),
                intent.getIntExtra("TaskFriendID", 0)
            )

        } else if (notifyTask != 0) {
            var taskEntities: TaskEntities
            taskViewModel.viewModelScope.launch {
                taskEntities = taskViewModel.getSingleTask(notifyTask)
                displayTaskInfo(
                    taskEntities.taskTitle,
                    taskEntities.taskDesc,
                    taskEntities.taskDesc,
                    taskEntities.taskTime,
                    taskEntities.taskLink,
                    taskEntities.taskImage,
                    taskEntities.taskColor.toInt(),
                    taskEntities.taskFriendID
                )
            }

        }
        setToastMessage("Notified Task ID $notifyTask", Color.parseColor("#1AC231"))

    }

    private fun selectAndCompressImage() {
        cropActivityResultLauncher =
            registerForActivityResult(userPermission.openGalleryAndGetImage) {
                if (it != null) {
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            imageFilePath = Compressor.compress(this@AddNewTask, it.toFile()).path
                        }
                        GlideApp.with(this@AddNewTask).load(imageFilePath)
                            .into(binding.taskImageView).clearOnDetach()
                    }
                } else {
                    Toast.makeText(applicationContext, "Selected Noun", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                setToastMessage("gallery permission granted", Color.parseColor("#1AC231"))
                cropActivityResultLauncher.launch("image/*")
            } else {
                setToastMessage("gallery permission refused", Color.RED)
            }
        }

    private fun displayTaskInfo(
        title: String?,
        desc: String?,
        date: String?,
        time: String?,
        link: String?,
        imagePath: String?,
        colorOfTask: Int?,
        friendID: Int?
    ) {
        runBlocking {

            binding.tiTaskTitle.setText(title!!)
            binding.tiTaskContent.setText(desc!!)
            binding.tvShowTaskTime.text = time!!
            binding.tvShowTaskDate.text = date!!
            binding.tvShowTaskLink.text = link!!
            taskColor = colorOfTask!!
            binding.bottomView.setBackgroundColor(taskColor)
            imageFilePath = imagePath!!
            val contactID = friendID

            GlideApp.with(this@AddNewTask).asBitmap().load(imageFilePath)
                .into(binding.taskImageView).clearOnDetach()
            if (friendID != 0) {
                val contact = taskViewModel.getSingleContact(contactID!!)
                GlideApp.with(this@AddNewTask).asBitmap().load(contact.contact_Image)
                    .into(binding.taskFriendImage).clearOnDetach()
                binding.tvShowTaskFriend.text = contact.contact_Name
            }
        }
        binding.btnDeleteTask.visibility = View.VISIBLE

    }

    private fun saveNewTask() {
        val title = binding.tiTaskTitle.text.toString()
        val desc = binding.tiTaskContent.text.toString()
        val time = binding.tvShowTaskTime.text.toString()
        val date = binding.tvShowTaskDate.text.toString()
        val link = binding.tvShowTaskLink.text.toString()

        val taskEntities = TaskEntities(
            title,
            desc,
            time,
            date,
            link,
            taskColor.toString(),
            imageFilePath,
            friendID
        )
        when (taskID) {

            0 -> {
                if (title.isEmpty() || desc.isEmpty() || time.isEmpty() || date.isEmpty()) {
                    setToastMessage("Please make sure all fields are filled", Color.RED)
                } else {
                    taskViewModel.saveNewTask(taskEntities)
                    alarmService.setExactAlarm(
                        taskAlarm,
                        "You Have A New Task  Called ${taskEntities.taskTitle} With Your Friend .. Let's Go To Do It.",
                        1,
                        taskEntities.taskId
                    )
                    setToastMessage("Task Saved", Color.parseColor("#1AC231"))
                    this.startNewActivity(HomeScreen::class.java, 1)
                }
            }
            intent.getIntExtra("ID", 0) -> {
                if (title == intent.getStringExtra("Title") && desc == intent.getStringExtra("Content") && time == intent.getStringExtra(
                        "Time"
                    )
                ) {
                    setToastMessage(
                        "Please make sure you've updated anything.",
                        Color.parseColor("#FC6C00")
                    )
                } else {
                    taskEntities.taskId = taskID
                    taskViewModel.updateCurrentTask(taskEntities)
                    alarmService.setExactAlarm(
                        taskAlarm,
                        "You Have A New Task  Called ${taskEntities.taskTitle} With Your Friend .. Let's Go To Do It.",
                        1,
                        taskEntities.taskId
                    )
                    this.startNewActivity(HomeScreen::class.java, 1)
                    setToastMessage("Task Updated", Color.parseColor("#1AC231"))
                }
            }

        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_BackToHomeMT -> {
                this.startNewActivity(HomeScreen::class.java, 1)
            }
            R.id.btn_OpenBottomSheet -> {
                TaskBottomSheet(
                    taskColor,
                    binding.tvShowTaskTime.text.toString(),
                    binding.tvShowTaskDate.text.toString(),
                    binding.tiTaskTitle.text.toString(),
                    binding.tvShowTaskLink.text.toString(),
                    friendID,
                    taskID
                ).show(supportFragmentManager, "TAG")
            }
            R.id.btn_DeleteTask -> {
                CustomDeleteDialog(taskID, 1).show(supportFragmentManager, "TAG")
            }
            R.id.btnOpenMyGallery -> {
                if (userPermission.checkUserLocationPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    cropActivityResultLauncher.launch("image/*")

                } else {
                    permReqLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                }
            }
            R.id.tv_ShowTaskLink -> {
                if (binding.tvShowTaskLink.text.isNullOrEmpty()) {
                    setToastMessage("There is no link included at this task", Color.RED)
                } else {
                    CustomWebView(binding.tvShowTaskLink.text.toString()).show(
                        supportFragmentManager,
                        "TAG"
                    )
                }
            }
            R.id.btn_SaveTask -> {
                saveNewTask()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.startNewActivity(HomeScreen::class.java, 1)
    }

    override fun setTaskInfo(
        color: String, lintText: String, time: String, date: String, friendID: Int, taskAlarm: Long
    ) {

        taskColor = color.toInt()
        this@AddNewTask.taskAlarm = taskAlarm
        binding.bottomView.setBackgroundColor(taskColor)
        binding.tvShowTaskTime.text = time
        binding.tvShowTaskDate.text = date
        binding.tvShowTaskLink.text = lintText
        if (friendID != 0) {
            var contactEntities: ContactEntities
            runBlocking {
                contactEntities = taskViewModel.getSingleContact(friendID)
            }
            this@AddNewTask.friendID = contactEntities.contactId
            GlideApp.with(this@AddNewTask).asBitmap().load(contactEntities.contact_Image)
                .into(binding.taskFriendImage).clearOnDetach()
            binding.tvShowTaskFriend.text = contactEntities.contact_Name
        }
    }
}