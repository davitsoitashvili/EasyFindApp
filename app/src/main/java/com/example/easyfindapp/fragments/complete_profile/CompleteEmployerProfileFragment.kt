package com.example.easyfindapp.fragments.complete_profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.easyfindapp.App
import com.example.easyfindapp.R
import com.example.easyfindapp.activities.HomeActivity
import com.example.easyfindapp.extensions.setImage
import com.example.easyfindapp.fragments.BaseFragment
import com.example.easyfindapp.tools.Tools
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.network.EndPoints
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.utils.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_complete_employer_profile.view.*
import kotlinx.android.synthetic.main.loader_layout.*
import org.json.JSONObject
import java.util.*

class CompleteEmployerProfileFragment : BaseFragment() {
    private var employerType: String? = null
    private var imageUri: Uri? = null
    private var gender: String? = null
    private var uploadedImage: String? = null
    override fun getFragmentLayout() = R.layout.fragment_complete_employer_profile
    override fun startFragmentConfiguration(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
    }

    private fun init() {
        manageEmployerTypeSpinner()
        displayCompletedFields()
        clickListeners()
        selectGender()
    }

    private fun clickListeners() {
        itemView!!.selectProfileImageEmployer.setOnClickListener {
            checkPermissions()
        }

        itemView!!.completeEmployerProfileBtn.setOnClickListener {
            if (employerType == PERSON) {
                checkPersonEmptyFields()
            } else {
                checkCompanyEmptyFields()
            }
        }
    }

    private fun selectGender() {
        itemView!!.selectPersonGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.genderMale -> gender = MALE
                R.id.genderFemale -> gender = FEMALE
            }
        }
    }

    private fun displayCompletedFields() {
        itemView!!.emailAddressEmployerView.text =
            UserPreference.getData(UserPreference.EMAIL_ADDRESS)
    }

    private fun manageEmployerTypeSpinner() {
        val types =
            arrayOf(resources.getString(R.string.person), resources.getString(R.string.company))
        itemView!!.selectEmployerTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        employerType = PERSON
                        itemView!!.companyNameTextInputLayout.visibility = View.INVISIBLE
                        itemView!!.personGroup.visibility = View.VISIBLE
                    }
                    1 -> {
                        employerType = COMPANY
                        itemView!!.personGroup.visibility = View.GONE
                        itemView!!.companyNameTextInputLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
        val spinnerAdapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, types)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        itemView!!.selectEmployerTypeSpinner.adapter = spinnerAdapter
    }

    private fun checkPersonEmptyFields() {
        val emailAddress = itemView!!.emailAddressEmployerView
        val userName = itemView!!.inputUserNameEmployer
        val age = itemView!!.inputAgeEmployer
        if (userName.text.isEmpty() || emailAddress.text.isEmpty() || age.text.isEmpty() || gender == null || uploadedImage == null) {
            Tools.errorDialog(
                requireActivity(),
                resources.getString(R.string.required_fields),
                resources.getString(R.string.fill_complete_employer_person_profile_required_fields),
                resources.getString(R.string.try_again)
            )
        } else {
            requestPersonProfileComplete(
                userName.text.toString(),
                emailAddress.text.toString(),
                gender.toString(),
                age.text.toString(),
                uploadedImage!!
            )
        }
    }

    private fun requestPersonProfileComplete(
        userName: String,
        emailAddress: String,
        gender: String,
        age: String,
        photoUrl: String
    ) {
        val parameters = mutableMapOf<String, String>()
        parameters["user_name"] = userName
        parameters["email_address"] = emailAddress
        parameters["gender"] = gender
        parameters["age"] = age.toString()
        parameters["photo_url"] = photoUrl
        ResponseLoader.getPostResponse(
            EndPoints.COMPLETE_PROFILE_EMPLOYER_PERSON,
            parameters,
            spinLoaderView,
            employerResponseCallback
        )
    }

    private fun checkCompanyEmptyFields() {
        val companyName = itemView!!.inputCompanyName
        val emailAddress = itemView!!.emailAddressEmployerView
        if (companyName.text.isEmpty() || emailAddress.text.isEmpty() || uploadedImage == null) {
            Tools.errorDialog(
                requireActivity(),
                resources.getString(R.string.required_fields),
                resources.getString(R.string.fill_complete_employer_company_profile_required_fields),
                resources.getString(R.string.try_again)
            )
        } else {
            requestCompanyProfileComplete(
                uploadedImage!!,
                companyName.text.toString(),
                emailAddress.text.toString()
            )
        }
    }

    private fun requestCompanyProfileComplete(
        companyLogo: String,
        companyName: String,
        emailAddress: String
    ) {
        val parameters = mutableMapOf<String, String>()
        parameters["company_logo"] = companyLogo
        parameters["company_name"] = companyName
        parameters["email_address"] = emailAddress
        ResponseLoader.getPostResponse(
            EndPoints.COMPLETE_PROFILE_EMPLOYER_COMPANY,
            parameters,
            spinLoaderView,
            employerResponseCallback
        )
    }

    private val employerResponseCallback = object : ResponseCallback {
        override fun onSuccess(response: String) {
            val responseJson = JSONObject(response)
            if (responseJson.has("profile_completed")) {
                val profileCompleted = responseJson.getBoolean("profile_completed")
                if (profileCompleted) {
                    UserPreference.saveData(UserPreference.EMPLOYER_TYPE, employerType!!)
                    openDashBoard()
                }
            }
        }

        override fun onFailure(response: String) {
            Toast.makeText(activity!!, response, Toast.LENGTH_LONG).show()
        }

        override fun onError(response: String) {
            Toast.makeText(activity!!, response, Toast.LENGTH_LONG).show()
        }

    }

    private fun openDashBoard() {
        val intent = Intent(requireActivity(), HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(checkReadExternalStoragePermission(
                    requireActivity().applicationContext
                ) && checkWriteExternalStoragePermission(
                    requireActivity().applicationContext
                ) && checkCameraPermission(
                    requireActivity().applicationContext
                ))
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                    ), PERMISSION_REQUEST_CODE
                )
            } else {
                imageUploadChooser()
            }
        }
    }

    private fun saveImage(imageUri: Uri) {
        val randomName: String = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("images/$randomName")
        ref.putFile(imageUri).addOnSuccessListener {
            Toast.makeText(
                App.appInstance?.applicationContext,
                "image was uploaded successfully",
                Toast.LENGTH_SHORT
            ).show()
            ref.downloadUrl.addOnSuccessListener {
                uploadedImage = it.toString()

            }
        }.addOnFailureListener {
            Toast.makeText(App.appInstance?.applicationContext, it.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size == 3) {
                if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[2] == PackageManager.PERMISSION_GRANTED
                            )
                ) {
                    Toast.makeText(
                        activity,
                        "Please confirm permission to continue",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    imageUploadChooser()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_IMAGE_FROM_CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (imageUri != null) {
                itemView!!.selectProfileImageEmployer.setImage(imageUri!!)
                saveImage(imageUri!!)
            }
        } else if (requestCode == TAKE_IMAGE_FROM_GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data!!.data != null) {
                itemView!!.selectProfileImageEmployer.setImage(data.data!!)
                saveImage(data.data!!)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun takeImageFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Picture from Camera")
        imageUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )

        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(camera, TAKE_IMAGE_FROM_CAMERA_REQUEST_CODE)
    }

    private fun takeImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, TAKE_IMAGE_FROM_GALLERY_REQUEST_CODE)
    }

    private fun imageUploadChooser() {
        Tools.uploadImageChooser(
            requireActivity(),
            resources.getString(R.string.choose),
            resources.getString(R.string.close)
        ) {
            when (it) {
                0 -> {
                    takeImageFromCamera()
                }
                1 -> {
                    takeImageFromGallery()
                }
            }
        }
    }
}