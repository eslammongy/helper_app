package com.eslammongy.helper.ui.module.contact

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eslammongy.helper.R
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.ActivityAddNewContactBinding
import com.eslammongy.helper.ui.dailogs.CustomDeleteDialog
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.ui.module.task_friends.TaskWithFriendAndSendEmail
import com.eslammongy.helper.utilis.*
import com.eslammongy.helper.viewModels.ContactViewMode
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import java.io.File

class AddNewContact : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddNewContactBinding
    private lateinit var contactViewMode: ContactViewMode
    private var contactColor: Int? = null
    private var contactID: Int = 0
    private var showing = false
    private var profileImagePath: String = "image"
    private var compressedProfileImage: File? = null
    private val userPermission by lazy { UserPermission(this) }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBackToHome.setOnClickListener(this)

        contactViewMode = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(ContactViewMode::class.java)
        contactColor = ResourcesCompat.getColor(resources, R.color.ColorDefaultNote, theme)
        contactID = intent.getIntExtra("ID", 0)
        if (contactID != 0){
            getSelectedContactInfo()
        }
        selectAndCompressImage()
        binding.btnOpenColorPicker.setOnClickListener(this)
        binding.btnDeleteCurrentContact.setOnClickListener(this)
        binding.btnBackToHome.setOnClickListener(this)
        binding.ChangeProfilePhoto.setOnClickListener(this)
        binding.btnSaveNewContact.setOnClickListener(this)
        binding.btnShowTwF.setOnClickListener(this)
        binding.btnOpenEmailForm.setOnClickListener(this)
        binding.btnCallMyContact.setOnClickListener(this)
        binding.chlPaletteColor.setOnColorSelectedListener { clr ->
            contactColor = clr
            binding.btnOpenColorPicker.setCardBackgroundColor(contactColor!!)
            binding.parentView.setBackgroundColor(contactColor!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getSelectedContactInfo() {
        contactViewMode.viewModelScope.launch(Dispatchers.Main) {
            binding.contactInputName.setText(intent.getStringExtra("Name"))
            binding.contactTopName.text = intent.getStringExtra("Name")
            binding.contactInputPhone.setText(intent.getStringExtra("Phone"))
            binding.contactInputEmail.setText(intent.getStringExtra("Email"))
            binding.contactInputAddress.setText(intent.getStringExtra("Address"))
            contactColor = Integer.parseInt(intent.getStringExtra("Color")!!)
            binding.btnOpenColorPicker.setCardBackgroundColor(contactColor!!)
            binding.chlPaletteColor.setSelectedColor(contactColor!!)
            binding.parentView.setBackgroundColor(contactColor!!)
            profileImagePath = intent.getStringExtra("ImagePath")!!
            GlideApp.with(this@AddNewContact).asBitmap().load(profileImagePath)
                .into(binding.contactProfilePhoto).clearOnDetach()
            binding.btnSaveNewContact.text = "Update"
            binding.btnDeleteCurrentContact.visibility = View.VISIBLE
            binding.btnCallMyContact.visibility = View.VISIBLE
            binding.btnOpenEmailForm.visibility = View.VISIBLE
            binding.btnShowTwF.visibility = View.VISIBLE

        }

    }

    private fun selectAndCompressImage() {
        cropActivityResultLauncher =
            registerForActivityResult(userPermission.openGalleryAndGetImage) {
                if (it != null) {
                    contactViewMode.viewModelScope.launch(Dispatchers.Main) {
                        compressedProfileImage =
                            Compressor.compress(this@AddNewContact, it.toFile())
                        profileImagePath = compressedProfileImage!!.path
                        GlideApp.with(this@AddNewContact).load(compressedProfileImage!!.path)
                            .into(binding.contactProfilePhoto)
                    }
                } else {
                    setToastMessage("Selected Noun" , Color.parseColor("#CB0003"))
                }
            }
    }

    private fun saveNewContact() {

        val contactName = binding.contactInputName.text.toString()
        val contactPhone = binding.contactInputPhone.text.toString()
        val contactEmail = binding.contactInputEmail.text.toString()
        val contactAddress = binding.contactInputAddress.text.toString()
        val contactEntities = ContactEntities(
            contactName,
            contactPhone,
            contactEmail,
            contactAddress,
            contactColor.toString(),
            profileImagePath
        )
        when (contactID) {
            0 -> {
                if (contactName.isEmpty() || contactPhone.isEmpty() || contactEmail.isEmpty() || contactAddress.isEmpty()) {
                    showingSnackBar(
                        binding.root,
                        "Please make sure all fields are filled",
                        "#F98404"
                    )
                } else {
                    contactViewMode.saveNewContact(contactEntities)
                    this.startNewActivity(HomeScreen::class.java, 3)
                    setToastMessage("Contact Saved", Color.parseColor("#6ECB63"))
                }
            }
            intent.getIntExtra("ID", 0) -> {
                contactEntities.contactId = contactID
                if (contactName == intent.getStringExtra("Name") && contactPhone == intent.getStringExtra(
                        "Phone"
                    )
                ) {
                    showingSnackBar(
                        binding.root,
                        "Please make sure you've updated anything.",
                        "#F98404"
                    )
                } else {
                    contactEntities.contactId = contactID
                    contactViewMode.updateCurrentContact(contactEntities)
                    this.startNewActivity(HomeScreen::class.java, 3)
                    setToastMessage("Contact Info Updated", Color.parseColor("#6ECB63"))
                }
            }

        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                setToastMessage("Gallery Permission Granted", Color.parseColor("#6ECB63"))
                cropActivityResultLauncher.launch("image/*")
            } else {
                setToastMessage("Gallery Permission Refused" , Color.parseColor("#CB0003"))
            }
        }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_SaveNewContact -> {
                saveNewContact()
            }
            R.id.btn_DeleteCurrentContact -> {
                CustomDeleteDialog(contactID, 3).show(supportFragmentManager, "TAG")
            }
            R.id.btn_CallMyContact -> {
                this.makePhoneCall(binding.contactInputPhone.text.toString())
            }
            R.id.btn_OpenEmailForm -> {
                val twfFragment = TaskWithFriendAndSendEmail(binding.contactInputName.text.toString(), binding.contactInputEmail.text.toString(), 0)
                twfFragment.show(supportFragmentManager, "Tag")
            }
            R.id.btn_ShowTwF -> {
                val twfFragment = TaskWithFriendAndSendEmail(binding.contactInputName.text.toString(), "ShowingTaskList", contactID)
                twfFragment.show(supportFragmentManager, "Tag")
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
                if (userPermission.checkUserLocationPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    cropActivityResultLauncher.launch("image/*")

                } else {
                    permReqLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                }
            }
            R.id.btn_BackToHome -> {
                this.startNewActivity(HomeScreen::class.java, 3)
            }

        }

    }

}