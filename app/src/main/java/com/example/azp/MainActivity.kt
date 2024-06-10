package com.example.azp

import ProfileFragment
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.azp.databinding.ActivityMainBinding
import com.example.azp.fragment.CalendarFragment
import com.example.azp.fragment.DocumentsFragment
import com.example.azp.fragment.GraphsFragment
import com.example.azp.fragment.ListFragment
import com.example.azp.fragment.MainFragment
import com.example.azp.utilities.AuthRepository
import com.example.azp.utilities.AuthViewModel
import com.example.azp.utilities.AuthViewModelFactory
import com.example.azp.utilities.UID
import com.example.azp.utilities.initFirebase
import com.example.azp.viewmodel.DocumentsViewModel
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    lateinit var documentsViewModel: DocumentsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initFields()

        val authRepository = AuthRepository()
        val model: AuthViewModel by viewModels {
            AuthViewModelFactory(authRepository)
        }
        val user=model.checkUser()
        Log.d("ProfileFrag", UID)
        if(!user){
            model.guestUser()
        }
        documentsViewModel = ViewModelProvider(this).get(DocumentsViewModel::class.java)


        //кнопка навигации
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false // Отключаем стандартную кнопку меню

        // Установка собственной иконки для кнопки навигации
        toggle.setHomeAsUpIndicator(R.drawable.b_menu)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        fragmentManager = supportFragmentManager

        openFragment(MainFragment())

        toggle.setToolbarNavigationClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {

                binding.profileEmail.text = model.getCurrentUser().value?.email.toString()
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        //открытие профиля
        binding.profileImage.setOnClickListener {
            openFragment(ProfileFragment())
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    //функция открытия страниц
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_list -> openFragment(ListFragment())
            R.id.nav_calendar -> openFragment(CalendarFragment())
            R.id.nav_graphs -> openFragment(GraphsFragment())
            R.id.nav_documents -> openFragment(DocumentsFragment())
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

    private fun initFields(){
        initFirebase()
    }

}
