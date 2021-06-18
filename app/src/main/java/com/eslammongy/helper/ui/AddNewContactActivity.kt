package com.eslammongy.helper.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.converter.Converter
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.ActivityAddNewContactBinding
import com.eslammongy.helper.fragment.TaskWithFriendFragment
import com.eslammongy.helper.fragment.dialogs.CustomDeleteDialog
import com.eslammongy.helper.helperfun.CompressImage
import com.eslammongy.helper.helperfun.GlideApp
import com.eslammongy.helper.helperfun.PickAndCropImage
import com.eslammongy.helper.helperfun.UserPermission
import com.yalantis.ucrop.UCrop


@SuppressLint("SetTextI18n")
class AddNewContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddNewContactBinding
    private var taskColor: Int? = null
    private val galleryPermissionCode = 101
    private var contactID: Int = 0
    private var showing = false
    private lateinit var imageConverter: Converter
    private val requestCropImage = 102
    private val pickAndCropImage by lazy { PickAndCropImage(this , requestCropImage) }
    private val userPermission by lazy { UserPermission(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageConverter = Converter()
        taskColor = ResourcesCompat.getColor(resources, R.color.ColorDefaultNote, theme)
        contactID = intent.getIntExtra("ID", 0)
        getSelectedContactInfo(contactID)
        binding.btnOpenColorPicker.setOnClickListener(this)
        binding.btnDeleteCurrentContact.setOnClickListener(this)
        binding.btnBackToHome.setOnClickListener(this)
        binding.ChangeProfilePhoto.setOnClickListener(this)
        binding.btnSaveNewContact.setOnClickListener(this)
        binding.btnShowTwF.setOnClickListener(this)
        binding.btnOpenEmailForm.setOnClickListener(this)
        binding.btnCallMyContact.setOnClickListener(this)
        binding.chlPaletteColor.setOnColorSelectedListener { clr ->
            taskColor = clr
            binding.btnOpenColorPicker.setCardBackgroundColor(taskColor!!)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_LONG)
                    .show()
                 //pickAndCropImage.pickImageFromGallery()
                val intent = Intent(Intent.ACTION_GET_CONTENT , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                intent.type = "image/*"
                val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
              //  intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            }
        }
        when (requestCode) {
            galleryPermissionCode -> innerCheck("Gallery")

        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCropImage && resultCode == Activity.RESULT_OK){

            val imageUri = data!!.data
            if (imageUri != null){
                pickAndCropImage.startCropImage(imageUri)
            }

        }else if(requestCode ==  UCrop.REQUEST_CROP && resultCode ==  RESULT_OK){

            val imageResultCrop = UCrop.getOutput(data!!)
            if (imageResultCrop != null){
                binding.contactProfilePhoto.setImageURI(imageResultCrop)
            }

        }
    }

    private fun backToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("ArrowKey", 3)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToHomeActivity()
    }

    private fun getSelectedContactInfo(ID: Int){
        if (ID != 0) {
            binding.contactInputName.setText(intent.getStringExtra("Name"))
            binding.contactTopName.text = intent.getStringExtra("Name")
            binding.contactInputPhone.setText(intent.getStringExtra("Phone"))
            binding.contactInputEmail.setText(intent.getStringExtra("Email"))
            binding.contactInputAddress.setText(intent.getStringExtra("Address"))
            taskColor = Integer.parseInt(intent.getStringExtra("Color")!!)
            binding.btnOpenColorPicker.setCardBackgroundColor(taskColor!!)
            binding.chlPaletteColor.setSelectedColor(taskColor!!)
            val contactPhoto = imageConverter.toBitMap(intent.getByteArrayExtra("ImagePath")!!)
            GlideApp.with(this).asBitmap().load(contactPhoto).into( binding.contactProfilePhoto).clearOnDetach()
            binding.btnSaveNewContact.text = "Update"
            binding.btnDeleteCurrentContact.visibility = View.VISIBLE
        }
    }
    private fun saveNewContact() {

        val contactName = binding.contactInputName.text.toString()
        val contactPhone = binding.contactInputPhone.text.toString()
        val contactEmail = binding.contactInputEmail.text.toString()
        val contactAddress = binding.contactInputAddress.text.toString()
        val imageDrawable = binding.contactProfilePhoto.drawable as BitmapDrawable
        val imageByteArray = imageConverter.fromBitMap(imageDrawable.bitmap)
        val reduceImageContactSize = CompressImage.reduceBitmapSize(imageConverter.toBitMap(imageByteArray), 250000)
        val contactEntities = ContactEntities(
            contactName,
            contactPhone,
            contactEmail,
            contactAddress,
            taskColor.toString(),
            reduceImageContactSize
        )

        when (contactID) {

            0 -> {
                if (contactName.isEmpty() || contactPhone.isEmpty() || contactEmail.isEmpty() || contactAddress.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Error required filed is empty.. $taskColor!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    HelperDataBase.getDataBaseInstance(this).contactDao()
                        .saveNewContact(contactEntities)
                    backToHomeActivity()
                    Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show()

                }
            }
            intent.getIntExtra("ID", 0) -> {

                if (contactName == intent.getStringExtra("Name") && contactPhone == intent.getStringExtra(
                        "Phone"
                    ) && contactEmail == intent.getStringExtra(
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
                    HelperDataBase.getDataBaseInstance(this).contactDao()
                        .updateCurrentContact(contactEntities)
                    backToHomeActivity()
                    Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    private fun makePhoneCall() {
        val phoneNumber = binding.contactInputPhone.text.toString()
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.btn_SaveNewContact -> {
                saveNewContact()
            }
            R.id.btn_DeleteCurrentContact -> {
                val dialogFragment = CustomDeleteDialog(contactID, 2)
                openFrameLayout(dialogFragment)
            }
            R.id.btn_CallMyContact -> {
                makePhoneCall()
            }
            R.id.btn_OpenEmailForm -> {
                openEmailAndTaskFragment(
                    binding.contactInputName.text.toString(),
                    binding.contactInputEmail.text.toString()
                )
            }
            R.id.btn_ShowTwF -> {
                openEmailAndTaskFragment(binding.contactInputName.text.toString(), "ShowTaskView")
            }
            R.id.btn_OpenColorPicker -> {
                if (showing) {
                    binding.chlPaletteColor.visibility = View.GONE
                    showing = false
                } else {
                    binding.chlPaletteColor.visibility = View.VISIBLE
                    showing = true
                }
            }
            R.id.Change_ProfilePhoto -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pickAndCropImage.pickImageFromGallery()
                } else {
                    userPermission.checkUserPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        "gallery",
                        galleryPermissionCode
                    )
                }

            }
            R.id.btn_BackToHome -> {
                backToHomeActivity()
            }

        }
    }

    private fun openEmailAndTaskFragment(name: String, email: String){
        if (binding.contactInputName.text!!.isEmpty() || contactID == 0){
            Toast.makeText(applicationContext, "there is no friend here !!", Toast.LENGTH_LONG).show()
        }else{
            val twfFragment = TaskWithFriendFragment(
                name,
                email
            )
            openFrameLayout(twfFragment)
        }
    }
    private fun openFrameLayout(fragment: Fragment) {

        binding.fragmentContainer.visibility = View.VISIBLE
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment)
        transaction.commit()

    }

    override fun onStop() {
        super.onStop()
        Glide.with(this).clear(binding.contactProfilePhoto)
    }
}