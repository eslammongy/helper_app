package com.eslammongy.helper.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.repository.ContactRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactViewMode(application: Application): AndroidViewModel(application) {

    private var contactRepo: ContactRepo
    var getAllContacts: LiveData<List<ContactEntities>>

    init {
        val contactDao = HelperDataBase.getDataBaseInstance(application).contactDao()
        contactRepo = ContactRepo(contactDao)
        getAllContacts = contactDao.getAllContacts()
    }

    fun saveNewContact(contactEntities: ContactEntities) = viewModelScope.launch (Dispatchers.IO){
        contactRepo.saveNewContact(contactEntities)
    }

    fun updateCurrentContact(contactEntities: ContactEntities) = viewModelScope.launch (Dispatchers.IO){
        contactRepo.updateCurrentContact(contactEntities)
    }

    fun deleteCurrentContact(contactEntities: ContactEntities) = viewModelScope.launch (Dispatchers.IO){
        contactRepo.deleteCurrentContact(contactEntities)
    }

    fun searchInContactDataBase(queryString: String): LiveData<List<ContactEntities>> {
        return contactRepo.searchInContactDataBase(queryString).asLiveData()
    }

    fun getTasksWithFriend(id:Int): LiveData<List<TaskEntities>> {
        return contactRepo.getTasksWithFriend(id)
    }


}