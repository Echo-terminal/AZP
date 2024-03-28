package com.example.azp

import ProfileFragment
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.azp.databinding.ActivityMainBinding
import com.example.azp.fragment.CalendarFragment
import com.example.azp.fragment.DocumentsFragment
import com.example.azp.fragment.GraphsFragment
import com.example.azp.fragment.ListFragment
import com.example.azp.fragment.MeetingsFragment
import com.example.azp.fragment.SettingsFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        //кнопка навигации
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false // Отключаем стандартную кнопку меню

        // Установка собственной иконки для кнопки навигации
        toggle.setHomeAsUpIndicator(R.drawable.b_menu)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        fragmentManager = supportFragmentManager

        toggle.setToolbarNavigationClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        //открытие профиля
        binding.profileImage.setOnClickListener {
            openFragment(ProfileFragment())
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

    }

    //создание правого меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    //функция открытия страниц
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_list -> openFragment(ListFragment())
            R.id.nav_calendar -> openFragment(CalendarFragment())
            R.id.nav_graphs -> openFragment(GraphsFragment())
            R.id.nav_meetings -> openFragment(MeetingsFragment())
            R.id.nav_documents -> openFragment(DocumentsFragment())
            R.id.nav_profile -> openFragment(ProfileFragment())
            R.id.nav_settings -> openFragment(SettingsFragment())
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    //функция открытия фрагмента
    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }


    //кнопка правого меню и открытие страниц
    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                openFragment(ProfileFragment())
                return true
            }
            R.id.nav_settings -> {
                openFragment(SettingsFragment())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }*/


}
