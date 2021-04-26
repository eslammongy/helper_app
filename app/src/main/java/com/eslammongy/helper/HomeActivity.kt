package com.eslammongy.helper

import android.app.Activity
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eslammongy.helper.fragment.CheckListFragment
import com.eslammongy.helper.fragment.ContactFragment
import com.eslammongy.helper.fragment.TaskFragment
import com.eslammongy.helper.fragment.WeatherFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class HomeActivity : AppCompatActivity() {

    companion object {

        const val ID_TASKS = 1
        const val ID_CHECKLIST = 2
        const val ID_CONTACT = 3
        const val ID_WEATHER = 4
    }
    private lateinit var bottomNavigationBar:MeowBottomNavigation
    private lateinit var btnOpenNewActivity:ImageButton
    private lateinit var titleFragment:TextView
    private var selectedFragment:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigationBar = findViewById(R.id.bottomNavigation)
        btnOpenNewActivity = findViewById(R.id.btn_AddNewElement)
        titleFragment = findViewById(R.id.title_ActiveFragment)
        bottomNavigationBar.add(MeowBottomNavigation.Model(ID_TASKS, R.drawable.ic_iconfinder_calendar))
        bottomNavigationBar.add(MeowBottomNavigation.Model(ID_CHECKLIST , R.drawable.ic_iconfinder_check_list))
        bottomNavigationBar.add(MeowBottomNavigation.Model(ID_CONTACT , R.drawable.ic_iconfinder_contact_user))
        bottomNavigationBar.add(MeowBottomNavigation.Model(ID_WEATHER , R.drawable.ic_iconfinder_snowflake))

        selectFragmentWhenBack()
       // bottomNavigationBar.show(ID_TASKS , true)

        bottomNavigationBar.setOnShowListener {

            when(it.id){

                ID_TASKS ->{
                    val taskFragment = TaskFragment()
                    replaceFragment(taskFragment)
                    titleFragment.text = getString(R.string.task)
                    hideSearchAddIcon()
                }
                ID_CHECKLIST -> {
                    val checkListFragment = CheckListFragment()
                    replaceFragment(checkListFragment)
                    titleFragment.text = getString(R.string.check_list)
                    hideSearchAddIcon()
                }
                ID_CONTACT -> {
                    val contactFragment = ContactFragment()
                    replaceFragment(contactFragment)
                    titleFragment.text = getString(R.string.contact)
                    hideSearchAddIcon()
                }
                ID_WEATHER -> {
                    val weatherFragment = WeatherFragment()
                    replaceFragment(weatherFragment)
                    titleFragment.text = getString(R.string.weather)
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
        fragmentTransition.replace(R.id.fragment_holder , fragment)
            .commit()
    }

    fun openNewSelectedActivity(view: View) {

        when(titleFragment.text){

            "Task" -> {
                openActivity(AddNewTaskActivity())
            }
            "CheckList" -> {
                openActivity(AddNewCheckListActivity())
            }
            "Contact" -> {
                openActivity(AddNewContactActivity())
            }

        }
    }

    private fun hideSearchAddIcon(){

        val addButton = findViewById<ImageButton>(R.id.btn_AddNewElement)
        val searchButton = findViewById<ImageButton>(R.id.btn_Search)
        val title = titleFragment.text.toString()
        if (title == "Weather"){

            addButton.visibility = View.GONE
            searchButton.visibility = View.GONE
        }else{
            addButton.visibility = View.VISIBLE
            searchButton.visibility = View.VISIBLE

        }
    }
    private fun openActivity(activity: Activity){
        val intent = Intent(this , activity::class.java)
        intent.putExtra("SearchKey" , titleFragment.text.toString())
        startActivity(intent)
        finish()
    }

    private fun selectFragmentWhenBack(){

        selectedFragment = intent.getIntExtra("ArrowKey" , 0)

        when(selectedFragment){

            ID_CHECKLIST ->{
                bottomNavigationBar.show(ID_CHECKLIST , true)
            }
            ID_CONTACT ->{
                bottomNavigationBar.show(ID_CONTACT , true)
            }
            else ->{
                bottomNavigationBar.show(ID_TASKS , true)
            }
        }
    }

    fun openSearchActivity(view: View) {
        when(titleFragment.text){

            "Task" -> {
                openActivity(SearchActivity())
            }
            "CheckList" -> {
                openActivity(SearchActivity())
            }
            "Contact" -> {
                openActivity(SearchActivity())
            }

        }

    }
}