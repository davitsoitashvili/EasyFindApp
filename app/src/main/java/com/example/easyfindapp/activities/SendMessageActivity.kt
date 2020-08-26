package com.example.easyfindapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easyfindapp.App
import com.example.easyfindapp.R
import com.example.easyfindapp.adapters.MessagesAdapter
import com.example.easyfindapp.models.MessageModel
import com.example.easyfindapp.models.UsernameAndPhotoModel
import com.example.easyfindapp.network.EndPoints.GET_USERNAME_AND_PHOTO
import com.example.easyfindapp.network.ResponseCallback
import com.example.easyfindapp.network.ResponseLoader
import com.example.easyfindapp.user_preference.UserPreference
import com.example.easyfindapp.utils.PHOTO_URL
import com.example.easyfindapp.utils.RECEIVER_ID
import com.example.easyfindapp.utils.USER_NAME
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_send_messege.*
import kotlinx.android.synthetic.main.loader_layout.*

class SendMessageActivity : AppCompatActivity() {
    private lateinit var receiverID: String
    private lateinit var senderID: String
    private var employerPhotoUrl: String = ""
    private var receiverUsername: String = ""
    lateinit var adapter: MessagesAdapter
    var list = mutableListOf<MessageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_messege)
        init()
    }

    private fun init() {
        employerPhotoUrl = intent.extras!!.getString(PHOTO_URL, "")
        receiverUsername = intent.extras!!.getString(USER_NAME, "")
        receiverID = intent.extras!!.getInt(RECEIVER_ID).toString()
        senderID = UserPreference.getData(UserPreference.USER_ID)!!

        val map = mutableMapOf<String, String>()
        map["receiverID"] = receiverID
        map["senderID"] = senderID.toString()

        ResponseLoader.getPostResponse(GET_USERNAME_AND_PHOTO, map, spinLoaderView, object :
            ResponseCallback {
            override fun onSuccess(response: String) {

                val usernameModel = Gson().fromJson(response, UsernameAndPhotoModel::class.java)
                getMessages(senderID!!, receiverID, usernameModel)
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

        listeners()
    }

    private fun listeners() {
        sendMessage.setOnClickListener {
            val message = messageText.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }
    }

    private fun sendMessage(message: String) {

        val ref =
            FirebaseDatabase.getInstance().getReference("messages/${senderID}/${receiverID}").push()
        val ref2 =
            FirebaseDatabase.getInstance().getReference("messages/${receiverID}/${senderID}").push()
        val ref3 =
            FirebaseDatabase.getInstance().getReference("lastMessages/${senderID}/${receiverID}")
        val ref4 =
            FirebaseDatabase.getInstance().getReference("lastMessages/${receiverID}/${senderID}")
        val message = MessageModel(message, senderID, receiverID, receiverUsername)
        ref.setValue(message).addOnSuccessListener {

        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
        ref2.setValue(message)
        ref3.setValue(message)
        ref4.setValue(message)
        messageText.text.clear()
    }

    private fun getMessages(senderID: String, rec: String, usernameModel: UsernameAndPhotoModel) {

        val ref = FirebaseDatabase.getInstance().getReference("messages/$senderID/$rec")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val message = p0.getValue(MessageModel::class.java)
                if (message != null) {
                    list.add(message)
                    adapter.notifyItemInserted(list.size - 1)
                    messageRecyclerview.scrollToPosition(
                        list.size - 1
                    )
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
        adapter = MessagesAdapter(
            list, usernameModel
        )
        messageRecyclerview.layoutManager = LinearLayoutManager(this)
        messageRecyclerview.adapter = adapter
    }
}