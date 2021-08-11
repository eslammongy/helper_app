package com.eslammongy.helper.ui.module.task

import android.Manifest
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toFile
import com.eslammongy.helper.R
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.ui.module.task_friends.TaskBottomSheet
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.ActivityAddNewTaskBinding
import com.eslammongy.helper.helpers.*
import com.eslammongy.helper.ui.dailogs.CustomDeleteDialog
import com.eslammongy.helper.ui.dailogs.CustomWebView
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AddNewTask : AppCompatActivity(), View.OnClickListener , CoroutineScope  ,  TaskBottomSheet.BottomSheetInterface{
    private  lateinit var binding: ActivityAddNewTaskBinding
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    private var taskColor: Int = 0
    private var taskID: Int = 0
    private var notifyTask:Int = 0
    private var friendID: Int = 0
    private var imageFilePath:String = "ImagePath"
    private val userPermission by lazy { UserPermission(this) }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
         job = Job()
        taskID = intent.getIntExtra("ID" , 0)
        notifyTask = intent.getIntExtra("NotifiedTaskID" , 0)
        binding.btnBackToHomeMT.setOnClickListener(this)
        binding.btnOpenBottomSheet.setOnClickListener(this)
        binding.btnDeleteTask.setOnClickListener(this)
        binding.btnOpenMyGallery.setOnClickListener(this)
        binding.btnSaveTask.setOnClickListener(this)
        binding.tvShowTaskLink.setOnClickListener(this)
        selectAndCompressImage()
        binding.bottomView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.ColorDefaultNote, theme))
        launch {
            if (taskID != 0){
                displayDateFromAdapter()

            }else{
                displayNotifiedTask()
            }
           /// setToastMessage("Notified Task ID $notifyTask")
        }
    }

    private fun selectAndCompressImage(){
        cropActivityResultLauncher = registerForActivityResult(userPermission.openGalleryAndGetImage){
            if (it != null){
                launch {
                    withContext(Dispatchers.IO){
                      imageFilePath = Compressor.compress(this@AddNewTask, it.toFile()).path
                    }
                    GlideApp.with(this@AddNewTask).load(imageFilePath).into(binding.taskImageView).clearOnDetach()
                }
            }else{
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
                setToastMessage("gallery permission granted")
                cropActivityResultLauncher.launch("image/*")
            }else{
                setToastMessage("gallery permission refused")
            }
        }

    private fun displayNotifiedTask(){

            launch {
                val taskEntities = HelperDataBase.getDataBaseInstance(this@AddNewTask).taskDao()
                    .getSingleTask(notifyTask)
                binding.tiTaskTitle.setText(taskEntities.taskTitle)
                binding.tiTaskContent.setText(taskEntities.taskDesc)
                binding.tvShowTaskTime.text = taskEntities.taskTime
                binding.tvShowTaskDate.text = taskEntities.taskDate
                binding.tvShowTaskLink.text = taskEntities.taskLink
                taskColor = taskEntities.taskColor.toInt()
                binding.bottomView.setBackgroundColor(taskColor)
                imageFilePath = taskEntities.taskImage
                friendID = taskEntities.taskFriendID


                GlideApp.with(this@AddNewTask).asBitmap().load(imageFilePath)
                    .into(binding.taskImageView).clearOnDetach()
                if (friendID != 0) {
                    val contact = HelperDataBase.getDataBaseInstance(this@AddNewTask).taskDao()
                        .getSingleContact(friendID)
                    GlideApp.with(this@AddNewTask).asBitmap().load(contact.contact_Image)
                        .into(binding.taskFriendImage).clearOnDetach()
                    binding.tvShowTaskFriend.text = contact.contact_Name
                }
            }
            binding.btnDeleteTask.visibility = View.VISIBLE

    }
    private fun displayDateFromAdapter(){
        if (taskID != 0) {
            binding.tiTaskTitle.setText(intent.getStringExtra("Title"))
            binding.tiTaskContent.setText(intent.getStringExtra("Content"))
            binding.tvShowTaskTime.text = intent.getStringExtra("Time")
            binding.tvShowTaskDate.text = intent.getStringExtra("Date")
            binding.tvShowTaskLink.text = intent.getStringExtra("Link")
            taskColor = intent.getStringExtra("Color")!!.toInt()
            binding.bottomView.setBackgroundColor(taskColor)
            imageFilePath = intent.getStringExtra("TaskImage")!!
            friendID = intent.getIntExtra("TaskFriendID" , 0)
            launch {
                GlideApp.with(this@AddNewTask).asBitmap().load(imageFilePath).into(binding.taskImageView).clearOnDetach()
                if (friendID != 0) {
                    val contact = HelperDataBase.getDataBaseInstance(this@AddNewTask).taskDao().getSingleContact(friendID)
                    GlideApp.with(this@AddNewTask).asBitmap().load(contact.contact_Image).into(binding.taskFriendImage).clearOnDetach()
                    binding.tvShowTaskFriend.text =contact.contact_Name
                }

            }
            binding.btnDeleteTask.visibility = View.VISIBLE

        }
    }

    private fun saveNewTask() {
        val title = binding.tiTaskTitle.text.toString()
        val desc = binding.tiTaskContent.text.toString()
        val time = binding.tvShowTaskTime.text.toString()
        val date = binding.tvShowTaskDate.text.toString()
        val link = binding.tvShowTaskLink.text.toString()

        val taskEntities =
            TaskEntities(title, desc, time, date, link, taskColor.toString(), imageFilePath ,  friendID)

        when (taskID) {

            0 -> {
                if (title.isEmpty() || desc.isEmpty() || time.isEmpty() || date.isEmpty()) {
                    showingSnackBar(binding.root , "Error required filed is empty." , "#FF5722")
                } else {

                    launch {
                        withContext(Dispatchers.IO) {
                            HelperDataBase.getDataBaseInstance(this@AddNewTask).taskDao()
                                .saveNewTask(taskEntities)
                        }
                    }
                    setToastMessage("Task Saved")
                    this.startNewActivity(HomeScreen::class.java , 1)
                }
            }
            intent.getIntExtra("ID", 0) -> {

                if (title == intent.getStringExtra("Title") && desc == intent.getStringExtra("Content") && time == intent.getStringExtra("Time")) {
                    showingSnackBar(binding.root , "Sorry You Don't Update Anything !!" , "#FF5722")
                } else {
                    taskEntities.taskId = taskID
                    launch {
                        withContext(Dispatchers.IO){
                            HelperDataBase.getDataBaseInstance(this@AddNewTask).taskDao().updateCurrentTask(taskEntities)

                        }
                    }
                    this.startNewActivity(HomeScreen::class.java , 1)
                    setToastMessage("Task Updated")
                }
            }

        }
    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_BackToHomeMT ->{
                this.startNewActivity(HomeScreen::class.java , 1)
            }
            R.id.btn_OpenBottomSheet ->{
                val color = (binding.bottomView.background as ColorDrawable).color
                TaskBottomSheet(
                    color,
                    binding.tvShowTaskTime.text.toString(),
                    binding.tvShowTaskDate.text.toString(), binding.tiTaskTitle.text.toString(), binding.tvShowTaskLink.text.toString(), friendID,taskID
                ).show(supportFragmentManager, "TAG")
            }
            R.id.btn_DeleteTask ->{
               CustomDeleteDialog(taskID , 1).show(supportFragmentManager, "TAG")
            }
            R.id.btnOpenMyGallery ->{
                if (userPermission.checkUserLocationPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    cropActivityResultLauncher.launch("image/*")

                }else{
                    permReqLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                }
            }
            R.id.tv_ShowTaskLink ->{
                if (binding.tvShowTaskLink.text.isNullOrEmpty()){
                    setToastMessage("There is no link included at this task")
                }else{
                    CustomWebView(binding.tvShowTaskLink.text.toString()).show(supportFragmentManager , "TAG")
                }
            }
            R.id.btn_SaveTask ->{
                saveNewTask()
            }

        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.startNewActivity(HomeScreen::class.java , 1)
    }

    override fun setTaskInfo(color: String, lintText: String, time: String, date: String, friendID: Int
    ) {
        launch {
            taskColor = color.toInt()
            binding.bottomView.setBackgroundColor(taskColor)
            binding.tvShowTaskTime.text = time
            binding.tvShowTaskDate.text = date
            binding.tvShowTaskLink.text = lintText
            if(friendID != 0){
                val contact = HelperDataBase.getDataBaseInstance(this@AddNewTask).taskDao().getSingleContact(friendID)
                this@AddNewTask.friendID = contact.contactId
                GlideApp.with(this@AddNewTask).asBitmap().load(contact.contact_Image).into(binding.taskFriendImage).clearOnDetach()
                binding.tvShowTaskFriend.text =contact.contact_Name
            }
        }
    }
}