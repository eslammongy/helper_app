@file:Suppress("DEPRECATION")

package com.eslammongy.helper.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eslammongy.helper.R
import com.eslammongy.helper.commonfun.PickAndCropImage
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.converter.Converter
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.ActivityAddNewTaskBinding
import com.eslammongy.helper.fragment.WebViewFragment
import com.eslammongy.helper.fragment.dialogs.CustomDeleteDialog
import com.eslammongy.helper.fragment.dialogs.TaskBottomSheet
import com.yalantis.ucrop.UCrop
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class AddNewTaskActivity : AppCompatActivity(), View.OnClickListener,
    TaskBottomSheet.BottomSheetInterface {
    private lateinit var binding: ActivityAddNewTaskBinding
    private val galleryPermissionCode = 101
    private val locationPermissionCode = 102
    private val pickImageCode = 1000
    private lateinit var imageConverter: Converter
    private lateinit var pickAndCropImage: PickAndCropImage
    private var imageResultCropping: Uri? = null
    private lateinit var startAnimation: Animation
    private lateinit var endAnimation: Animation
    private var taskColor: Int = 0
    private var taskID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //*****/
        pickAndCropImage = PickAndCropImage(this, pickImageCode)
        imageConverter = Converter()
        taskID = intent.getIntExtra("ID", 0)
        binding.bottomView.setBackgroundColor(resources.getColor(R.color.ColorDefaultNote))
        displayDateFromAdapter()
        startAnimation = AnimationUtils.loadAnimation(this, R.anim.starting_animation)
        endAnimation = AnimationUtils.loadAnimation(this, R.anim.ending_animation)
        binding.btnSaveTask.setOnClickListener(this)
        binding.btnDeleteTask.setOnClickListener(this)
        binding.btnBackToHomeMT.setOnClickListener(this)
        binding.btnOpenBottomSheet.setOnClickListener(this)
        binding.btnOpenMyGallery.setOnClickListener(this)
        binding.tvShowTaskLink.setOnClickListener(this)


    }

    private fun displayDateFromAdapter(){
        if (taskID != 0) {

            binding.tiTaskTitle.setText(intent.getStringExtra("Title"))
            binding.tiTaskContent.setText(intent.getStringExtra("Content"))
            binding.tvShowTaskTime.text = intent.getStringExtra("Time")
            binding.tvShowTaskDate.text = intent.getStringExtra("Date")
            binding.tvShowTaskLink.text = intent.getStringExtra("Link")
            binding.tvShowTaskFriend.text = intent.getStringExtra("FriendName")
            taskColor = intent.getStringExtra("Color")!!.toInt()
            binding.bottomView.setBackgroundColor(taskColor)
            binding.taskImageView.setImageBitmap(imageConverter.toBitMap(intent.getByteArrayExtra("ImagePath")!!))
            binding.taskFriendImage.setImageBitmap(imageConverter.toBitMap(intent.getByteArrayExtra("TaskImagePath")!!))
            binding.btnDeleteTask.visibility = View.VISIBLE

        }
    }

    private fun checkUserPermission(permission: String, name: String, requestCode: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(
                        applicationContext,
                        "$name Permission Granted.",
                        Toast.LENGTH_LONG
                    ).show()
                    pickAndCropImage.pickImageFromGallery()
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    showRequestPermissionDialog(permission, name, requestCode)
                }
                else -> {
                    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
                }
            }
        }
    }

    private fun showRequestPermissionDialog(permissions: String, name: String, requestCode: Int) {

        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("You need to access your $name permission is required to use this app")
            setTitle("Permission Required")
            setPositiveButton("Ok") { _, _ ->

                ActivityCompat.requestPermissions(
                    this@AddNewTaskActivity,
                    arrayOf(permissions),
                    requestCode
                )
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_LONG)
                    .show()
                pickAndCropImage.pickImageFromGallery()
            }
        }
        when (requestCode) {

            galleryPermissionCode -> innerCheck("Gallery")
            locationPermissionCode -> innerCheck("Location")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageCode) {
            imageResultCropping = data!!.data!!
            pickAndCropImage.startCropImage(imageResultCropping!!)
        } else if (requestCode == UCrop.REQUEST_CROP) {

            imageResultCropping = UCrop.getOutput(data!!)
            if (imageResultCropping != null) binding.taskImageView.setImageURI(imageResultCropping)
        }
    }

    private fun saveNewTask() {
        val converter = Converter()
        val title = binding.tiTaskTitle.text.toString()
        val desc = binding.tiTaskContent.text.toString()
        val time = binding.tvShowTaskTime.text.toString()
        val date = binding.tvShowTaskDate.text.toString()
        val link = binding.tvShowTaskLink.text.toString()
        val friendName = binding.tvShowTaskFriend.text.toString()
        val imageTaskDrawable = binding.taskImageView.drawable as BitmapDrawable
        val imageByteArray = converter.fromBitMap(imageTaskDrawable.bitmap)
        val imageTaskFDrawable = binding.taskFriendImage.drawable as BitmapDrawable
        val friendImageByteArray = converter.fromBitMap(imageTaskFDrawable.bitmap)

        val taskEntities =
            TaskEntities(title, desc, time, date, link, taskColor.toString(), imageByteArray , friendName , friendImageByteArray)

        when (taskID) {

            0 -> {
                if (title.isEmpty() || desc.isEmpty() || time.isEmpty() || date.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Error required filed is empty.. $taskColor!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    HelperDataBase.getDataBaseInstance(this).taskDao().saveNewTask(taskEntities)
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show()

                }
            }
            intent.getIntExtra("ID", 0) -> {

                if (title == intent.getStringExtra("Title") && desc == intent.getStringExtra("Content") && time == intent.getStringExtra(
                        "Time"
                    ) && date == intent.getStringExtra("Date")
                ) {
                    Toast.makeText(
                        this,
                        "Sorry You Don't Update Anything In This Task",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    taskEntities.taskId = taskID
                    HelperDataBase.getDataBaseInstance(this).taskDao().updateCurrentTask(
                        taskEntities
                    )
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()

                }
            }

        }

    }


    private fun backToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToHomeActivity()
    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.btn_SaveTask -> {
                saveNewTask()
            }
            R.id.btn_DeleteTask -> {

                val dialogFragment = CustomDeleteDialog(taskID , 1)
                openFrameLayout(dialogFragment)
            }
            R.id.btn_BackToHomeMT -> {
                backToHomeActivity()
            }
            R.id.btn_OpenBottomSheet -> {
               openTaskBottomSheet()
            }
            R.id.btnOpenMyGallery -> {
                checkUserPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    "gallery",
                    galleryPermissionCode
                )
            }
            R.id.tv_ShowTaskLink -> {
                val fragment = WebViewFragment(binding.tvShowTaskLink.text.toString())
                openFrameLayout(fragment)
            }

        }

    }

    private fun openTaskBottomSheet(){
        val color = (binding.bottomView.background as ColorDrawable).color
        val imageTaskFDrawable = binding.taskFriendImage.drawable as BitmapDrawable
        TaskBottomSheet(
            color,
            binding.tvShowTaskTime.text.toString(),
            binding.tvShowTaskDate.text.toString(),
            binding.tvShowTaskLink.text.toString(),
            binding.tvShowTaskFriend.text.toString(),
            imageConverter.fromBitMap(imageTaskFDrawable.bitmap)
        ).show(supportFragmentManager, "TAG")
    }

    private fun openFrameLayout(fragment:Fragment){
        binding.fragmentContainer.visibility = View.VISIBLE
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun setTaskInfo(
        color: String,
        lintText: String,
        time: String,
        date: String,
        friendName: String,
        friendImage: ByteArray
    ) {
        taskColor = color.toInt()
        binding.bottomView.setBackgroundColor(taskColor)
        binding.tvShowTaskTime.text = time
        binding.tvShowTaskDate.text = date
        binding.tvShowTaskLink.text = lintText
        binding.tvShowTaskFriend.text = friendName
        binding.taskFriendImage.setImageBitmap(imageConverter.toBitMap(friendImage))
    }

}