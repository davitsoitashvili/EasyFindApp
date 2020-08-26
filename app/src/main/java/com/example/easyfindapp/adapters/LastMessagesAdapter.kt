package com.example.easyfindapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.easyfindapp.App
import com.example.easyfindapp.Interfaces.RecyclerViewCallback
import com.example.easyfindapp.R
import com.example.easyfindapp.extensions.setImage
import com.example.easyfindapp.models.MessageModel
import com.example.easyfindapp.models.UsernameAndPhotoModel
import com.example.easyfindapp.network.EndPoints.GET_USERNAME_AND_PHOTO
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.user_preference.UserPreference
import com.google.gson.Gson
import kotlinx.android.synthetic.main.lastest_messeges.view.*

class LastMessagesAdapter(
    private val messages: MutableList<MessageModel>,
    val callBack: RecyclerViewCallback
) : RecyclerView.Adapter<LastMessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.lastest_messeges, parent, false)
        )
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var userModel: MessageModel? = null
        fun onBind() {
            userModel = messages[adapterPosition]

            loadPhoto(userModel!!, itemView)

            itemView.mainItem.setOnClickListener {
                callBack.clicked(adapterPosition)
            }
        }
    }

    private fun loadPhoto(userModel: MessageModel, itemView: View) {
        val map = mutableMapOf<String, String>()
        if (UserPreference.getData(UserPreference.USER_ID) == userModel.senderId) {
            map["receiverID"] = userModel.receiverId.toString()
            map["senderID"] = userModel.senderId.toString()
        } else {
            map["receiverID"] = userModel.senderId.toString()
            map["senderID"] = userModel.receiverId.toString()
        }

        ResponseLoader.getPostResponse(GET_USERNAME_AND_PHOTO, map, object :
            ResponseCallback {
            override fun onSuccess(response: String) {

                val usernameModel = Gson().fromJson(response, UsernameAndPhotoModel::class.java)
                itemView.lastMessage.text = userModel.message
                itemView.userUsername.text = usernameModel.receiverUSername
                itemView.userMessagePhotoView.setImage(Uri.parse(usernameModel.receiverPhotoUrl))
            }

            override fun onFailure(response: String) {
                Toast.makeText(App.appInstance!!.applicationContext, response, Toast.LENGTH_LONG)
                    .show()
            }

            override fun onError(response: String) {
                Toast.makeText(App.appInstance!!.applicationContext, response, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}