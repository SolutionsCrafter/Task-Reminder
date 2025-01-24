package com.example.collegealerts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.collegealerts.fragments.AlertFragment
import com.example.collegealerts.fragments.CalendarFragment
import com.example.collegealerts.fragments.HomeFragment
import com.example.collegealerts.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        fragmentAndNavBarInit()

        val fab = findViewById<FloatingActionButton>(R.id.fbtn)
        fab.setOnClickListener {
            startActivity(Intent(this,addTask::class.java))
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private  fun fragmentAndNavBarInit(){
        val navBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val homeFragment=HomeFragment()
        val calendarFragment=CalendarFragment()
        val alertFragment=AlertFragment()
        val settingsFragment=SettingsFragment()

        navBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_home->setCurrentFragment(homeFragment)
                R.id.menu_calendar->setCurrentFragment(calendarFragment)
                R.id.menu_alerts->setCurrentFragment(alertFragment)
                R.id.menu_settings->setCurrentFragment(settingsFragment)
            }
            true
        }

        setCurrentFragment(homeFragment)

    }

    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frag_view,fragment)
            commit()
        }

}