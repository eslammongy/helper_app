package com.eslammongy.helper.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.ActivityHomeBinding
import com.eslammongy.helper.fragment.CheckListFragment
import com.eslammongy.helper.fragment.ContactFragment
import com.eslammongy.helper.fragment.TaskFragment
import com.eslammongy.helper.fragment.WeatherFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import java.util.*

class HomeActivity : AppCompatActivity() {

    companion object {

        const val ID_TASKS = 1
        const val ID_CHECKLIST = 2
        const val ID_CONTACT = 3
        const val ID_WEATHER = 4
    }

    private lateinit var binding:ActivityHomeBinding
    private var selectedFragment:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val greetingCode = intent.getIntExtra("ToastMessage" , 0)
        if (greetingCode == 101) getGreetingMessage()
        binding.bottomNavigation.add(MeowBottomNavigation.Model(
            ID_TASKS,
            R.drawable.ic_iconfinder_calendar
        ))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(
            ID_CHECKLIST ,
            R.drawable.ic_iconfinder_check_list
        ))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(
            ID_CONTACT ,
            R.drawable.ic_iconfinder_contact_user
        ))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(
            ID_WEATHER ,
            R.drawable.ic_iconfinder_snowflake
        ))

        selectFragmentWhenBack()
       // bottomNavigationBar.show(ID_TASKS , true)

        binding.bottomNavigation.setOnShowListener {

            when(it.id){

                ID_TASKS ->{
                    val taskFragment = TaskFragment()
                    replaceFragment(taskFragment)
                    binding.titleActiveFragment.text = getString(R.string.task)
                    hideSearchAddIcon()
                }
                ID_CHECKLIST -> {
                    val checkListFragment = CheckListFragment()
                    replaceFragment(checkListFragment)
                    binding.titleActiveFragment.text = getString(R.string.check_list)
                    hideSearchAddIcon()
                }
                ID_CONTACT -> {
                    val contactFragment = ContactFragment()
                    replaceFragment(contactFragment)
                    binding.titleActiveFragment.text = getString(R.string.contact)
                    hideSearchAddIcon()
                }
                ID_WEATHER -> {
                    val weatherFragment = WeatherFragment()
                    replaceFragment(weatherFragment)
                    binding.titleActiveFragment.text = getString(R.string.weather)
                    hideSearchAddIcon()

                }
                else ->{
                    val taskFragment = TaskFragment()
                    replaceFragment(taskFragment)
                }
            }
        }

    }

    private fun replaceFragment(fragment:Fragment){

        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragment_holder, fragment)
            .commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openNewSelectedActivity(view: View) {

        when(binding.titleActiveFragment.text){

            "Task" -> {
                openActivity(AddNewTaskActivity() , "None")
            }
            "CheckList" -> {
                openActivity(AddNewCheckListActivity(), "None")
            }
            "Contact" -> {
                openActivity(AddNewContactActivity(), "None")
            }

        }
    }

    private fun hideSearchAddIcon(){

        val title = binding.titleActiveFragment.text.toString()
        if (title == "Weather"){

            binding.btnAddNewElement.visibility = View.GONE
            binding.btnSearch.visibility = View.GONE
        }else{
            binding.btnAddNewElement.visibility = View.VISIBLE
            binding.btnSearch.visibility = View.VISIBLE

        }
    }
    private fun openActivity(activity: Activity , text:String){
        val intent = Intent(this , activity::class.java)
        intent.putExtra("SearchKey" , text)
        startActivity(intent)
        finish()
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

    fun openSearchActivity(view: View) {
        when(binding.titleActiveFragment.text){

            "Task" -> {
                openActivity(SearchActivity(), "Task" )
            }
            "CheckList" -> {
                openActivity(SearchActivity(), "CheckList" )
            }
            "Contact" -> {
                openActivity(SearchActivity(), "Contact")
            }

        }

    }

    private fun setToastMessage(greetingMessage:String){
        val toast = Toast.makeText(this, greetingMessage, Toast.LENGTH_LONG)
        val view = toast.view
        view!!.background.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        toast.show()
    }

    private fun getGreetingMessage(){

        val calender = Calendar.getInstance()
        val handEmo = "\uD83D\uDC4B"
        when (calender.get(Calendar.HOUR_OF_DAY)) {

            in 0..11 -> setToastMessage("Good Morning  $handEmo")
            in 12..15 -> setToastMessage("Good Afternoon $handEmo")
            in 16..20 -> setToastMessage("Good Evening $handEmo")
            in 21..23 -> setToastMessage("Good Night $handEmo")
            else -> Toast.makeText(this , "Hello" , Toast.LENGTH_LONG).show()
        }


    }


}