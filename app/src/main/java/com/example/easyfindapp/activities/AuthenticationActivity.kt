package com.example.easyfindapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.easyfindapp.R
import com.example.easyfindapp.fragments.authentication.SignInFragment
import com.example.easyfindapp.fragments.authentication.SignUpFragment

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        openAuthenticationFragment(SignInFragment(), R.id.authenticationContainer, "SignInFragment")
    }

    private fun openAuthenticationFragment(fragment: Fragment, container: Int, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            container,
            fragment,
            tag
        )
        transaction.commit()
    }
}