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

class HomeActivity : AppCompatActivity(), View.OnClickListener {

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

        binding.btnAddNewElement.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
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
            .commitAllowingStateLoss()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openNewSelectedActivity() {

        when(binding.titleActiveFragment.text){

            "Task" -> {
                openActivity(AddNewTaskActivity() , "None" ,0)
            }
            "CheckList" -> {
                openActivity(AddNewCheckListActivity(), "None" ,0)
            }
            "Contact" -> {
                openActivity(AddNewContactActivity(), "None" ,0)
            }

        }
    }

    private fun hideSearchAddIcon(){

        when(binding.titleActiveFragment.text.toString()){
            "Task" ->{
                binding.btnAddNewElement.visibility = View.VISIBLE
                binding.btnSearch.visibility = View.VISIBLE

            }
            "Contact" ->{
                binding.btnAddNewElement.visibility = View.VISIBLE
                binding.btnSearch.visibility = View.VISIBLE

            }
            "CheckList" ->{
                binding.btnAddNewElement.visibility = View.VISIBLE
                binding.btnSearch.visibility = View.VISIBLE
            }
            "Weather" ->{
                binding.btnAddNewElement.visibility = View.GONE
                binding.btnSearch.visibility = View.GONE
            }
        }

    }
    private fun openActivity(activity: Activity , text:String , searchID:Int){
        val intent = Intent(this , activity::class.java)
        intent.putExtra("SearchKey" , text)
        intent.putExtra("SearchID" , searchID)
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

    private fun openSearchActivity() {
        when(binding.titleActiveFragment.text){

            "Task" -> {
                openActivity(SearchActivity(), "Task"  , 1)
            }
            "CheckList" -> {
                openActivity(SearchActivity(), "CheckList" , 3 )
            }
            "Contact" -> {
                openActivity(SearchActivity(), "Contact" , 2)
            }

        }

    }

    @Suppress("DEPRECATION")
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
       when(v!!.id){
           R.id.btn_AddNewElement ->{
               openNewSelectedActivity()
           }
           R.id.btn_Search ->{
             openSearchActivity()
           }
       }
    }

}