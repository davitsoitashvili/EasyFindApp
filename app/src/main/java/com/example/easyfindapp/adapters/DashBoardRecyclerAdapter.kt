package com.example.easyfindapp.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.easyfindapp.R
import com.example.easyfindapp.extensions.setImage
import com.example.easyfindapp.models.PostModel
import com.example.easyfindapp.models.DeveloperUserModel
import com.example.easyfindapp.utils.DEVELOPER
import com.example.easyfindapp.utils.DEVELOPERS_LAYOUT
import com.example.easyfindapp.utils.POST
import com.example.easyfindapp.utils.POSTS_LAYOUT
import kotlinx.android.synthetic.main.developer_recyclerview_item.view.*
import kotlinx.android.synthetic.main.posts_recycler_view_item.view.*

class DashBoardRecyclerAdapter(
    private val posts: MutableList<PostModel>?,
    private val developers: MutableList<DeveloperUserModel>?,
    private val positionCallback: (Int, String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == POSTS_LAYOUT) {
            PostViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.posts_recycler_view_item,
                    parent,
                    false
                )
            )
        } else {
            DeveloperViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.developer_recyclerview_item,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = posts?.size ?: developers!!.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            holder.onBindPosts(posts!!)
            holder.getRecyclerItemPosition(positionCallback)
        } else if (holder is DeveloperViewHolder) {
            holder.onBindDevelopers(developers!!)
            holder.getItemPosition(positionCallback)
        }
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var postModel: PostModel
        var skills: String = ""

        @SuppressLint("SetTextI18n")
        fun onBindPosts(posts: MutableList<PostModel>) {
            postModel = posts[adapterPosition]
            itemView.title.text = postModel.title.capitalize()
            itemView.experience.text =
                "${itemView.resources.getString(R.string.experience_level)}: ${postModel.experienceLevel}"
            itemView.description.text =
                "${itemView.resources.getString(R.string.description)}: ${postModel.description}"
            (0 until postModel.skills.size).forEach {
                skills += " ${postModel.skills[it]}"
            }
            itemView.requiredSkills.text = skills
            itemView.postAuthorView.text = "Author : ${postModel.emailAddress}"
        }

        fun getRecyclerItemPosition(positionCallback: (Int, String) -> Unit) {
            itemView.postRecyclerViewContainer.setOnClickListener {
                positionCallback.invoke(adapterPosition, POST)
            }
        }
    }

    inner class DeveloperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var developerModel: DeveloperUserModel
        fun onBindDevelopers(developers: MutableList<DeveloperUserModel>) {
            developerModel = developers[adapterPosition]
            itemView.developerPositionRecyclerItem.text = developerModel.position.capitalize()
            itemView.developerImageRecyclerItem.setImage(Uri.parse(developerModel.photoUrl))
            itemView.developerUserNameRecyclerItem.text = developerModel.userName.capitalize()
        }

        fun getItemPosition(positionCallback: (Int, String) -> Unit) {
            itemView.developerRecyclerViewContainer.setOnClickListener {
                positionCallback.invoke(adapterPosition, DEVELOPER)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (posts == null) {
            DEVELOPERS_LAYOUT
        } else {
            POSTS_LAYOUT
        }
    }
}