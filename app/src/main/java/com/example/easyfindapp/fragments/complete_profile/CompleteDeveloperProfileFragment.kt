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
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyfindapp.*
import com.example.easyfindapp.activities.HomeActivity
import com.example.easyfindapp.adapters.SkillsRecyclerAdapter
import com.example.easyfindapp.extensions.setImage
import com.example.easyfindapp.fragments.BaseFragment
import com.example.easyfindapp.models.CompleteProfileModel
import com.example.easyfindapp.network.EndPoints
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.tools.Tools
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_complete_developer_profile.view.*
import kotlinx.android.synthetic.main.loader_layout.*
import java.util.*

class CompleteDeveloperProfileFragment : BaseFragment() {
    private var imageUri: Uri? = null
    private lateinit var skillsRecyclerAdapter: SkillsRecyclerAdapter
    private val skills: MutableList<String> = mutableListOf()
    private var gender: String? = null
    private var uploadedImage: String? = null
    override fun getFragmentLayout() = R.layout.fragment_complete_developer_profile

    override fun startFragmentConfiguration(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
    }

    private fun init() {
        selectGender()
        displayCompletedFields()
        clickListeners()
        manageSkillRecyclerView()
    }

    private fun clickListeners() {
        itemView!!.addSkillBtn.setOnClickListener {
            getSkillValue()
        }

        itemView!!.completeDeveloperProfileBtn.setOnClickListener {
            checkEmptyFields()
        }

        itemView!!.selectProfileImageDeveloper.setOnClickListener {
            checkPermissions()
        }
    }

    private fun checkEmptyFields() {
        val userName = itemView!!.inputUserNameDeveloper
        val position = itemView!!.inputPosition
        val age = itemView!!.inputAgeDeveloper
        if (userName.text.isEmpty() || position.text.isEmpty() || age.text.isEmpty() || gender == null || uploadedImage == null || skills.size == 0) {
            Tools.errorDialog(
                activity!!,
                resources.getString(R.string.required_fields),
                resources.getString(R.string.fill_complete_developers_profile_required_fields),
                resources.getString(R.string.try_again)
            )
        } else {
            requestCompleteDeveloperProfile(
                userName.text.toString(),
                itemView!!.emailAddressDeveloperView.text.toString(),
                age.text.toString(),
                gender.toString(),
                position.text.toString(),
                uploadedImage!!,
                UserPreference.getData(UserPreference.ROLE)!!,
                skills
            )
        }
    }

    private fun requestCompleteDeveloperProfile(
        userName: String,
        emailAddress: String,
        age: String,
        gender: String,
        position: String,
        profileImageUrl: String,
        role: String,
        skills: MutableList<String>

    ) {
        val completeProfileModel = CompleteProfileModel(
            userName,
            emailAddress,
            age,
            gender,
            position,
            profileImageUrl,
            skills,
            role
        )

        val stringJson = Gson().toJson(completeProfileModel)
        val parameters = mutableMapOf<String, String>()
        parameters["json"] = stringJson
        ResponseLoader.getPostResponse(
            EndPoints.COMPLETE_PROFILE_DEVELOPERS,
            parameters,
            spinLoaderView,
            object : ResponseCallback {
                override fun onSuccess(response: String) {
                    openDashBoard()
                }

                override fun onFailure(response: String) {
                    Toast.makeText(activity!!, response, Toast.LENGTH_LONG).show()
                }

                override fun onError(response: String) {
                    Toast.makeText(activity!!, response, Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun openDashBoard() {
        val intent = Intent(requireActivity(), HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun getSkillValue() {
        if (itemView!!.inputAddSkillsField.text.isEmpty()) {
            Toast.makeText(requireActivity(), "Fill this field to add new skill", Toast.LENGTH_LONG).show()
        } else {
            skills.add(itemView!!.inputAddSkillsField.text.toString())
            itemView!!.inputAddSkillsField.text.clear()
            skillsRecyclerAdapter.notifyDataSetChanged()
        }
    }


    private fun selectGender() {
        itemView!!.selectUserGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.genderMale -> gender = MALE
                R.id.genderFemale -> gender = FEMALE
            }
        }
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

    @SuppressLint("SetTextI18n")
    private fun displayCompletedFields() {
        itemView!!.emailAddressDeveloperView.text =
            UserPreference.getData(UserPreference.EMAIL_ADDRESS)
        itemView!!.userRoleTextView.text = "Role: ${UserPreference.getData(UserPreference.ROLE)}"
    }

    private fun takeImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, TAKE_IMAGE_FROM_GALLERY_REQUEST_CODE)
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

    private fun manageSkillRecyclerView() {
        itemView!!.skillsRecyclerView.layoutManager =
            LinearLayoutManager(
                requireActivity().applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        skillsRecyclerAdapter = SkillsRecyclerAdapter(skills)
        itemView!!.skillsRecyclerView.adapter = skillsRecyclerAdapter
        skillsRecyclerAdapter.notifyDataSetChanged()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_IMAGE_FROM_CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (imageUri != null) {
                itemView!!.selectProfileImageDeveloper.setImage(imageUri!!)
                saveImage(imageUri!!)
            }
        } else if (requestCode == TAKE_IMAGE_FROM_GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data!!.data != null) {
                itemView!!.selectProfileImageDeveloper.setImage(data.data!!)
                saveImage(data.data!!)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
}