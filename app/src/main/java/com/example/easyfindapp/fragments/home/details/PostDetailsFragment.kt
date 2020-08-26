package com.example.easyfindapp.fragments.home.details

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyfindapp.R
import com.example.easyfindapp.activities.SendMessageActivity
import com.example.easyfindapp.adapters.SkillsRecyclerAdapter
import com.example.easyfindapp.extensions.changeImage
import com.example.easyfindapp.fragments.BaseFragment
import com.example.easyfindapp.models.PostModel
import com.example.easyfindapp.network.EndPoints
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.PHOTO_URL
import com.example.easyfindapp.utils.RECEIVER_ID
import com.example.easyfindapp.utils.USER_NAME
import kotlinx.android.synthetic.main.fragment_developer_details.view.*
import kotlinx.android.synthetic.main.fragment_post_details.view.*
import org.json.JSONObject

class PostDetailsFragment : BaseFragment() {
    var postModel: PostModel? = null
    private var changeImage: Boolean = true
    private var imageUrl: String? = null
    override fun getFragmentLayout() = R.layout.fragment_post_details
    override fun startFragmentConfiguration(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
        favouritePost()
        favouritePostsResponse(EndPoints.CHECK_POST)
    }

    private fun init() {
        if (postModel != null) {
            displayPostDetails(postModel!!)
        }
        itemView!!.messageEmployer.setOnClickListener {
            val intent = Intent(context, SendMessageActivity()::class.java)
            intent.putExtra(RECEIVER_ID, postModel!!.id)
            intent.putExtra(PHOTO_URL, postModel!!.photoUrl)
            startActivity(intent)
        }
    }

    private fun favouritePost() {
        itemView!!.favouritePost.setOnClickListener {
            itemView!!.favouritePost.changeImage(
                changeImage, R.drawable.ic_favourited,
                R.drawable.ic_favourite
            )

            if (changeImage) {
                favouritePostsResponse(
                    EndPoints.ADD_POST_TO_FAVOURITES
                )
            } else {
                favouritePostsResponse(
                    EndPoints.REMOVE_POST_FROM_FAVOURITES
                )
            }

            changeImage = !changeImage
        }
    }

    private fun favouritePostsResponse(
        path: String
    ) {
        ResponseLoader.getPostResponse(
            path,
            mutableMapOf(
                "user_id" to UserPreference.getData(UserPreference.USER_ID)!!,
                "favorited_user_id" to postModel!!.postID.toString()
            ),
            null,
            object : ResponseCallback {
                override fun onSuccess(response: String) {
                    checkFavouritePost(response)
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

    private fun checkFavouritePost(response: String) {
        val json = JSONObject(response)
        if (json.has("isFavorite")) {
            if (json.getBoolean("isFavorite")) {
                changeImage = false
                itemView!!.favouritePost.setImageResource(R.drawable.ic_favourited)
            } else {
                changeImage = true
                itemView!!.favouritePost.setImageResource(R.drawable.ic_favourite)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayPostDetails(postModel: PostModel) {
        imageUrl = postModel.photoUrl
        itemView!!.titleDetails.text = postModel.title.capitalize()
        itemView!!.experienceDetails.text =
            "${itemView!!.resources.getString(R.string.experience_level)}: ${postModel.experienceLevel}"
        itemView!!.descriptionDetails.text =
            "${itemView!!.resources.getString(R.string.description)}: ${postModel.description}"
        manageRequiredSkillsRecyclerView(postModel.skills)
        itemView!!.postAuthorDetailsView.text = "Author : ${postModel.emailAddress}"
    }

    private fun manageRequiredSkillsRecyclerView(skills: MutableList<String>) {
        itemView!!.requiredSkillsRecyclerViewDetails.layoutManager =
            LinearLayoutManager(requireActivity())
        itemView!!.requiredSkillsRecyclerViewDetails.adapter = SkillsRecyclerAdapter(skills)
    }
}