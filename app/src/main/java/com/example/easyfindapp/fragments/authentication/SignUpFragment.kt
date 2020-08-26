package com.example.easyfindapp.fragments.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.example.easyfindapp.R
import com.example.easyfindapp.activities.CompleteProfileActivity
import com.example.easyfindapp.activities.HomeActivity
import com.example.easyfindapp.extensions.emailValidation
import com.example.easyfindapp.fragments.BaseFragment
import com.example.easyfindapp.models.SignUpResponseModel
import com.example.easyfindapp.network.EndPoints
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.tools.Tools
import com.example.easyfindapp.tools.Tools.saveUserInformation
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.DEVELOPER
import com.example.easyfindapp.utils.EMPLOYER
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import kotlinx.android.synthetic.main.loader_layout.*

class SignUpFragment : BaseFragment() {
    private var emailValid: Boolean = false
    private var role: String? = null
    private var emailAddress: String? = null
    override fun getFragmentLayout() = R.layout.fragment_sign_up

    override fun startFragmentConfiguration(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
    }

    private fun init() {
        clickListeners()
        checkUserRole()
        checkEmailValidation()

    }

    private fun clickListeners() {
        itemView!!.alreadyHaveAccountView.setOnClickListener {
            openSignInFragment()
        }

        itemView!!.signUpButton.setOnClickListener {
            checkEmptyFields()
        }
    }

    private fun checkEmailValidation() {
        itemView!!.inputEmailAddressSignUp.emailValidation {
            emailValid = it
        }
    }

    private fun openSignInFragment() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.authenticationContainer,
            SignInFragment(), "SignInFragment"
        )
        transaction.addToBackStack("SignInFragment")
        transaction.commit()
    }

    private fun checkEmptyFields() {
        if (inputEmailAddressSignUp.text.isEmpty() || inputPasswordSignUp.text.isEmpty() ||
            inputConfirmPasswordSignUp.text.isEmpty()
        ) {
            Tools.errorDialog(
                activity!!,
                resources.getString(R.string.required_fields),
                resources.getString(R.string.fill_sign_up_required_fields),
                resources.getString(R.string.try_again)
            )
        } else if (!emailValid) {
            Tools.errorDialog(
                activity!!,
                resources.getString(R.string.input_field_validation),
                resources.getString(R.string.fill_valid_email),
                resources.getString(R.string.try_again)
            )
        } else if (inputPasswordSignUp.text.toString() != inputConfirmPasswordSignUp.text.toString()) {
            Tools.errorDialog(
                activity!!,
                resources.getString(R.string.password_mismatch),
                resources.getString(R.string.match_password),
                resources.getString(R.string.try_again)
            )
        } else if (role == null) {
            Tools.errorDialog(
                activity!!,
                resources.getString(R.string.required_fields),
                resources.getString(R.string.select_role),
                resources.getString(R.string.try_again)
            )
        } else {
            sendSignUpRequest(
                inputEmailAddressSignUp.text.toString(),
                inputPasswordSignUp.text.toString(),
                role!!
            )
            emailAddress = inputEmailAddressSignUp.text.toString()

        }
    }

    private fun sendSignUpRequest(email: String, password: String, role: String) {
        val parameters = mutableMapOf<String, String>()
        parameters["email_address"] = email
        parameters["password"] = password
        parameters["role"] = role
        ResponseLoader.getPostResponse(
            EndPoints.SIGN_UP,
            parameters,
            spinLoaderView,
            object : ResponseCallback {
                override fun onSuccess(response: String) {
                    parseSignUpResponse(response)
                }

                override fun onFailure(response: String) {
                    Toast.makeText(activity, response, Toast.LENGTH_LONG).show()
                }

                override fun onError(response: String) {
                    Toast.makeText(activity, response, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun openDashBoardActivity() {
        val intent = if (UserPreference.getData(UserPreference.ROLE)!!.isNotEmpty()) {
            Intent(activity!!.applicationContext, CompleteProfileActivity::class.java)
        } else {
            Intent(activity!!.applicationContext, HomeActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity!!.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun parseSignUpResponse(response: String) {
        val signUpResponseModel = Gson().fromJson(response, SignUpResponseModel::class.java)
        if (signUpResponseModel.registered) {
            saveUserInformation(signUpResponseModel.userID, role!!, emailAddress!!)
            openDashBoardActivity()
        } else {
            Toast.makeText(activity, signUpResponseModel.status, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkUserRole(): String? {
        itemView!!.selectUserRole.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.employerPosition -> {
                    role = EMPLOYER
                }
                R.id.developerPosition -> {
                    role = DEVELOPER
                }
            }
        }
        return role
    }
}