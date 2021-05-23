package com.eslammongy.helper.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eslammongy.helper.R
import com.eslammongy.helper.crop.PickAndCropImage
import com.eslammongy.helper.database.Converter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.ActivityAddNewContactBinding
import com.yalantis.ucrop.UCrop

class AddNewContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddNewContactBinding
    private var taskColor: Int? = null
    private val galleryPermissionCode = 101
    private val locationPermissionCode = 102
    private val pickImageCode = 1000
    var contactID:Int = 0
    private lateinit var imageConverter: Converter
    private lateinit var pickAndCropImage: PickAndCropImage
    private var imageResultCropping: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickAndCropImage = PickAndCropImage(this, pickImageCode)
        imageConverter = Converter()

        contactID = intent.getIntExtra("ID" , 0)
        if (contactID != 0){
            binding.contactInputName.setText(intent.getStringExtra("Name"))
            binding.contactInputPhone.setText(intent.getStringExtra("Phone"))
            binding.contactInputEmail.setText(intent.getStringExtra("Email"))
            binding.contactInputAddress.setText(intent.getStringExtra("Address"))
            taskColor = Integer.parseInt(intent.getStringExtra("Color")!!)
            binding.btnOpenColorPicker.setCardBackgroundColor(taskColor!!)
            binding.chlPaletteColor.setSelectedColor(taskColor!!)

            binding.contactProfilePhoto.setImageBitmap(imageConverter.toBitMap(intent.getByteArrayExtra("ImagePath")!!))
        }
        binding.btnOpenColorPicker.setOnClickListener(this)
        binding.btnBackToHome.setOnClickListener(this)
        binding.ChangeProfilePhoto.setOnClickListener(this)
        binding.btnSaveNewContact.setOnClickListener(this)
        binding.chlPaletteColor.setSelectedColor(resources.getColor(R.color.ColorDefaultNote))
        binding.chlPaletteColor.setOnColorSelectedListener { clr ->
            taskColor = clr
            binding.btnOpenColorPicker.setCardBackgroundColor(taskColor!!)
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
                    this@AddNewContactActivity,
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
            if (imageResultCropping != null) binding.contactProfilePhoto.setImageURI(imageResultCropping)
        }


    }
    private fun backToHomeActivity() {
        val intent = Intent(this , HomeActivity::class.java)
        intent.putExtra("ArrowKey" , 3)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
       backToHomeActivity()
    }

    private fun saveNewContact() {

        val contactName = binding.contactInputName.text.toString()
        val contactPhone = binding.contactInputPhone.text.toString()
        val contactEmail = binding.contactInputEmail.text.toString()
        val contactAddress = binding.contactInputAddress.text.toString()
        //val contactColor = binding.contactInputName.text.toString()
        val imageDrawable = binding.contactProfilePhoto.drawable as BitmapDrawable
        val imageByteArray = imageConverter.fromBitMap(imageDrawable.bitmap)
        val contactEntities = ContactEntities(contactName , contactPhone , contactEmail , contactAddress , taskColor.toString() , imageByteArray)

        when (contactID) {

            0 -> {
                if (contactName.isEmpty() || contactPhone.isEmpty() || contactEmail.isEmpty() || contactAddress.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Error required filed is empty.. $taskColor!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    HelperDataBase.getDataBaseInstance(this).contactDao().saveNewContact(contactEntities)
                    backToHomeActivity()
                    Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show()

                }
            }
            intent.getIntExtra("ID", 0) -> {

                if (contactName == intent.getStringExtra("Name") && contactPhone == intent.getStringExtra("Phone") && contactEmail == intent.getStringExtra(
                        "Email"
                    ) && contactAddress == intent.getStringExtra("Address")
                ) {
                    Toast.makeText(
                        this,
                        "Sorry You Don't Update Anything In This Task",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    contactEntities.contactId = contactID
                    HelperDataBase.getDataBaseInstance(this).contactDao().updateCurrentContact(contactEntities)
                   backToHomeActivity()
                    Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }
//    fun deleteCurrentContact() {}
//    fun makePhoneCall() {}
//    fun sendEmailMessage() {}
//    fun shareMyContact() {}
//    fun pickContactProfilePhoto() {}

    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.btn_SaveNewContact ->{
                saveNewContact()
            }
            R.id.btn_DeleteCurrentContact ->{}
            R.id.btn_CallMyContact ->{}
            R.id.btn_SendEmailMessage ->{}
            R.id.btn_ShareMyContact ->{}
            R.id.btn_OpenColorPicker ->{
                binding.chlPaletteColor.visibility = View.VISIBLE
            }
            R.id.Change_ProfilePhoto ->{
                checkUserPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    "gallery",
                    galleryPermissionCode
                )
            }
            R.id.btn_BackToHome ->{
                backToHomeActivity()
            }

        }
    }
}