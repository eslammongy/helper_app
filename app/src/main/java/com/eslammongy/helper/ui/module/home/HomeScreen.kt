package com.eslammongy.helper.ui.module.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.ActivityHomeScreenBinding
import com.eslammongy.helper.utilis.setToastMessage
import com.eslammongy.helper.ui.module.checklist.CheckListFragment
import com.eslammongy.helper.ui.module.contact.ContactsFragment
import com.eslammongy.helper.ui.module.task.TasksFragment
import com.eslammongy.helper.ui.module.weather.WeatherFragment
import com.eslammongy.helper.utilis.getGreetingMessage
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class HomeScreen : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityHomeScreenBinding
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    companion object {
        const val ID_TASKS = 1
        const val ID_CHECKLIST = 2
        const val ID_CONTACT = 3
        const val ID_WEATHER = 4
    }
    private var selectedFragment:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()
        binding.bottomNavigation .add(MeowBottomNavigation.Model(ID_TASKS, R.drawable.ic_calendar))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(ID_CHECKLIST, R.drawable.ic_iconfinder_check_list))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(ID_CONTACT, R.drawable.ic_iconfinder_contact_user))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(ID_WEATHER, R.drawable.ic_snowflake))
        selectFragmentWhenBack()
        val greetingCode = intent.getIntExtra("ToastMessage" , 0)
        if (greetingCode == 101) getGreetingMessage()
        binding.bottomNavigation.setOnShowListener  {
            when(it.id){
                ID_TASKS ->{
                    val taskFragment = TasksFragment()
                    replaceFragment(taskFragment)
                }
                ID_CHECKLIST -> {
                    val checkListFragment = CheckListFragment()
                    replaceFragment(checkListFragment)
                }
                ID_CONTACT -> {
                    val contactFragment = ContactsFragment()
                    replaceFragment(contactFragment)

                }
                ID_WEATHER -> {
                    val weatherFragment = WeatherFragment()
                    replaceFragment(weatherFragment)
                }
            }
        }

    }

    private fun selectFragmentWhenBack(){

        selectedFragment = intent.getIntExtra("ArrowKey" , 0)
        when(selectedFragment){
            ID_CHECKLIST ->{
                binding.bottomNavigation.show(ID_CHECKLIST , true)
            }
            ID_CONTACT ->{
                binding.bottomNavigation.show(ID_CONTACT , true)
            }
            else ->{
                binding.bottomNavigation.show(ID_TASKS , true)
            }
        }
    }

    private fun replaceFragment(fragment:androidx.fragment.app.Fragment){
        launch {
            val fragmentTransition = supportFragmentManager.beginTransaction()
            fragmentTransition.replace(R.id.fragment_holder, fragment)
                .disallowAddToBackStack()
                .commit()
        }

    }

    override fun onStop() {
        job.cancel()
        Glide.with(this).clear(binding.fragmentHolder)
        super.onStop()
        finish()
    }

    override fun onDestroy() {
        job.cancel()
        //Glide.with(this).clear(binding.fragmentHolder)
        super.onDestroy()
        finish()
    }
}