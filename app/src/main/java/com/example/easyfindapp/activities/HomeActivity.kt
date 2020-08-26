package com.example.easyfindapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.easyfindapp.R
import com.example.easyfindapp.adapters.HomeViewPagerAdapter
import com.example.easyfindapp.fragments.home.DashboardFragment
import com.example.easyfindapp.fragments.home.FavouritesFragment
import com.example.easyfindapp.fragments.home.MessagesFragment
import com.example.easyfindapp.fragments.home.ProfileFragment
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.EMPLOYER
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
    }

    private fun init() {
        manageHomeViewPager()
        listeners()
        if (UserPreference.getData(UserPreference.ROLE) != EMPLOYER)
            addButton.visibility = View.GONE
    }

    private fun manageHomeViewPager() {
        homeViewPager.adapter = HomeViewPagerAdapter(
            arrayOf(
                DashboardFragment(),
                MessagesFragment(),
                FavouritesFragment(),
                ProfileFragment()
            )
            , supportFragmentManager
        )

        nav_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboardNavigation -> homeViewPager.currentItem = 0
                R.id.messagesNavigation -> homeViewPager.currentItem = 1
                R.id.favouritesNavigation -> homeViewPager.currentItem = 2
                R.id.profileNavigation -> homeViewPager.currentItem = 3
            }
            true
        }

        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                nav_view.menu.getItem(position).isChecked = true
            }

        })
    }

    private fun listeners() {
        addButton.setOnClickListener {
            val intent = Intent(this, CreatePostEmployerActivity::class.java)
            startActivity(intent)
        }
    }
}