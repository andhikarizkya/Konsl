package com.example.konsl.psychologist.ui.consultations.confirmed.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.konsl.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class ConsultationCounselorChatViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val messages = MutableLiveData<ArrayList<Message>>()

    fun loadMessages(consultationId: String){
        db.collection("consultations")
            .document(consultationId)
            .collection("messages")
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.w(this::class.java.simpleName, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val listItems = ArrayList<Message>()
                for (doc in value) {
                    val item = Message(
                        id = doc.id,
                        message = doc.data["message"] as String,
                        senderId = doc.data["sender_id"] as String,
                        createdAt = doc.data["created_at"] as Timestamp
                    )
                    listItems.add(item)
                    Log.d(this::class.java.simpleName, "${doc.id} => ${doc.data}")
                }
                listItems.sortByDescending { it.createdAt }
                messages.postValue(listItems)
            }
    }

    fun getMessages(): LiveData<ArrayList<Message>>{
        return messages
    }
}