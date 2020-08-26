package com.example.easyfindapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.easyfindapp.R
import com.example.easyfindapp.fragments.complete_profile.CompleteDeveloperProfileFragment
import com.example.easyfindapp.fragments.complete_profile.CompleteEmployerProfileFragment
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.DEVELOPER

class CompleteProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)
        init()
    }

    private fun init() {
        chooseRequiredFragment()
    }

    private fun chooseRequiredFragment() {
        if (UserPreference.getData(UserPreference.ROLE) == DEVELOPER) {
            openCompleteProfileFragment(
                CompleteDeveloperProfileFragment(),
                R.id.completeProfileContainer,
                "CompleteDeveloperProfileFragment"
            )
        } else {
            openCompleteProfileFragment(
                CompleteEmployerProfileFragment(),
                R.id.completeProfileContainer,
                "CompleteEmployerProfileFragment"
            )
        }
    }

    private fun openCompleteProfileFragment(fragment: Fragment, container: Int, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            container,
            fragment,
            tag
        )
        transaction.commit()
    }
}