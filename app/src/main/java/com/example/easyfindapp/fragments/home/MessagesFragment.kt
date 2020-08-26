package com.example.easyfindapp.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyfindapp.Interfaces.RecyclerViewCallback
import com.example.easyfindapp.R
import com.example.easyfindapp.activities.SendMessageActivity
import com.example.easyfindapp.adapters.LastMessagesAdapter
import com.example.easyfindapp.fragments.BaseFragment
import com.example.easyfindapp.models.MessageModel
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.RECEIVER_ID
import com.example.easyfindapp.utils.USER_NAME
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.android.synthetic.main.fragment_messages.view.*

class MessagesFragment : BaseFragment() {
    private val lastMessageMap = mutableMapOf<String?, MessageModel?>()
    private var list = mutableListOf<MessageModel>()
    private lateinit var adapter: LastMessagesAdapter
    override fun getFragmentLayout() = R.layout.fragment_messages

    override fun startFragmentConfiguration(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        init()
    }

    private fun init() {
        getLastMessages()
    }

    private fun getLastMessages() {
        val userId = UserPreference.getData(UserPreference.USER_ID).toString()
        val ref = FirebaseDatabase.getInstance().getReference("/lastMessages/$userId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val lastMessage = p0.getValue(MessageModel::class.java)
                lastMessageMap[p0.key] = lastMessage
                addAdapter(lastMessageMap)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val lastMessage = p0.getValue(MessageModel::class.java)
                lastMessageMap[p0.key] = lastMessage
                addAdapter(lastMessageMap)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    private fun addAdapter(map: MutableMap<String?, MessageModel?>) {

        itemView!!.lastMessagesRecycler.layoutManager = LinearLayoutManager(context)

        list.clear()
        map.values.forEach() {
            if (it != null) {
                list.add(0, it)
            }
        }

        itemView!!.lastMessagesRecycler.layoutManager = LinearLayoutManager(context)

        adapter = LastMessagesAdapter(list, object : RecyclerViewCallback {
            override fun clicked(position: Int) {
                var receiverid = 0
                val intent = Intent(context, SendMessageActivity()::class.java)
                if (UserPreference.getData(UserPreference.USER_ID) == list[position].senderId!!) receiverid =
                    list[position].receiverId!!.toInt()
                else receiverid = list[position].senderId!!.toInt()
                intent.putExtra(RECEIVER_ID, receiverid)
                intent.putExtra(USER_NAME, list[position].receiverUsername)
                startActivity(intent)
            }
        })

        itemView!!.lastMessagesRecycler?.adapter = adapter
    }
}