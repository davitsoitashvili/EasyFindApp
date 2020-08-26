package com.example.easyfindapp.adapters

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater.*
import com.example.easyfindapp.R
import com.example.easyfindapp.extensions.setImage
import com.example.easyfindapp.models.MessageModel
import com.example.easyfindapp.models.UsernameAndPhotoModel
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.TO_MESSAGE
import com.example.easyfindapp.utils.FROM_MESSAGE
import kotlinx.android.synthetic.main.from_message_view.view.*
import kotlinx.android.synthetic.main.to_message_view.view.*

class MessagesAdapter(
    val messages: MutableList<MessageModel>,
    val userNameModel: UsernameAndPhotoModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TO_MESSAGE) {
            return ToMessageViewHolder(
                from(parent.context).inflate(
                    R.layout.to_message_view,
                    parent,
                    false
                )
            )
        }
        return FromMessageViewHolder(
            from(parent.context).inflate(
                R.layout.from_message_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ToMessageViewHolder) holder.onBind()
        else if (holder is FromMessageViewHolder) holder.onBind()

    }

    inner class ToMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var messageModel: MessageModel
        fun onBind() {
            messageModel = messages[adapterPosition]
            itemView.myMessage.text = messageModel.message
            itemView.myPhotoView.setImage(Uri.parse(userNameModel.senderPhotoUrl))
        }
    }

    inner class FromMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var model: MessageModel
        fun onBind() {
            model = messages[adapterPosition]
            itemView.messageFromView.text = model.message
            itemView.userPhotoMessageView.setImage(Uri.parse(userNameModel.receiverPhotoUrl))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val userID = UserPreference.getData(UserPreference.USER_ID)
        if (messages[position].senderId == userID) return TO_MESSAGE
        return FROM_MESSAGE
    }
}
