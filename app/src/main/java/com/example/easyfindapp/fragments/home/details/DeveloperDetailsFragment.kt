package com.example.easyfindapp.fragments.home.details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyfindapp.R
import com.example.easyfindapp.activities.FullScreenImageActivity
import com.example.easyfindapp.activities.SendMessageActivity
import com.example.easyfindapp.adapters.SkillsRecyclerAdapter
import com.example.easyfindapp.extensions.changeImage
import com.example.easyfindapp.extensions.setImage
import com.example.easyfindapp.fragments.BaseFragment
import com.example.easyfindapp.models.DeveloperUserModel
import com.example.easyfindapp.network.EndPoints
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.IMAGE_URL
import com.example.easyfindapp.utils.RECEIVER_ID
import com.example.easyfindapp.utils.USER_NAME
import kotlinx.android.synthetic.main.fragment_complete_developer_profile.view.*
import kotlinx.android.synthetic.main.fragment_developer_details.view.*
import org.json.JSONObject

class DeveloperDetailsFragment : BaseFragment() {
    var developerUserModel: DeveloperUserModel? = null
    private var changeImage: Boolean = true
    private var imageUrl: String? = null
    override fun getFragmentLayout() = R.layout.fragment_developer_details

    override fun startFragmentConfiguration(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
    }

    private fun init() {
        if (developerUserModel != null) {
            displayDeveloperDetails(developerUserModel!!)
        }
        clickListeners()
        favouriteDeveloper()
        favouriteDeveloperResponse(EndPoints.CHECK_DEVELOPER)
    }

    private fun clickListeners() {
        itemView!!.messageDeveloper.setOnClickListener() {
            val intent = Intent(context, SendMessageActivity()::class.java)
            intent.putExtra(RECEIVER_ID, developerUserModel!!.id)
            intent.putExtra(USER_NAME, developerUserModel!!.userName)
            startActivity(intent)
        }

        itemView!!.userProfileImageDetails.setOnClickListener {
            openFullScreenImage()
        }
    }

    private fun favouriteDeveloper() {
        itemView!!.favouriteDeveloper.setOnClickListener {
            itemView!!.favouriteDeveloper.changeImage(
                changeImage,
                R.drawable.ic_favourited,
                R.drawable.ic_favourite
            )

            if (changeImage) {
                favouriteDeveloperResponse(
                    EndPoints.ADD_DEVELOPER_TO_FAVOURITES
                )
            } else {
                favouriteDeveloperResponse(
                    EndPoints.REMOVE_DEVELOPER_FROM_FAVOURITES
                )
            }

            changeImage = !changeImage
        }
    }


    private fun favouriteDeveloperResponse(
        path: String
    ) {
        ResponseLoader.getPostResponse(
            path,
            mutableMapOf(
                "user_id" to UserPreference.getData(UserPreference.USER_ID)!!,
                "favorited_user_id" to developerUserModel!!.id.toString()
            ),
            null,
            object : ResponseCallback {
                override fun onSuccess(response: String) {
                    checkFavouriteDeveloper(response)
                }

                override fun onFailure(response: String) {
                    Toast.makeText(activity!!, response, Toast.LENGTH_LONG).show()
                }

                override fun onError(response: String) {
                    Toast.makeText(activity!!, response, Toast.LENGTH_LONG).show()
                }

            }
        )
    }

    private fun checkFavouriteDeveloper(response: String) {
        val json = JSONObject(response)
        if (json.has("isFavorite")) {
            if (json.getBoolean("isFavorite")) {
                changeImage = false
                itemView!!.favouriteDeveloper.setImageResource(R.drawable.ic_favourited)
            } else {
                changeImage = true
                itemView!!.favouriteDeveloper.setImageResource(R.drawable.ic_favourite)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayDeveloperDetails(developerUserModel: DeveloperUserModel) {
        imageUrl = developerUserModel.photoUrl
        itemView!!.userRoleViewDetails.text = developerUserModel.role
        itemView!!.userProfileImageDetails.setImage(Uri.parse(developerUserModel.photoUrl))
        itemView!!.userUserNameViewDetails.text = "Username: ${developerUserModel.userName}"
        itemView!!.userEmailAddressViewDetails.text =
            "Email Address: ${developerUserModel.emailAddress}"
        itemView!!.userPositionViewDetails.text = "Position: ${developerUserModel.position}"
        itemView!!.userGenderViewDetails.text = "Gender: ${developerUserModel.gender}"
        itemView!!.userAgeViewDetails.text = "Age: ${developerUserModel.age}"
        manageSkillsRecyclerView(developerUserModel.skills)
    }

    private fun manageSkillsRecyclerView(skills: MutableList<String>) {
        itemView!!.userSkillsRecyclerViewDetails.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        itemView!!.userSkillsRecyclerViewDetails.adapter = SkillsRecyclerAdapter(skills)
    }

    private fun openFullScreenImage() {
        val intent = Intent(requireActivity(), FullScreenImageActivity::class.java)
        intent.putExtra(IMAGE_URL, imageUrl)
        startActivity(intent)
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}