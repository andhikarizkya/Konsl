package com.example.konsl.psychologist.ui.consultations.request

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.konsl.model.Consultation
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ConsultationRequestViewModel : ViewModel() {
    companion object {
        const val STATUS_WAITING_FOR_CONFIRMATION = "menunggu konfirmasi"
        const val STATUS_CONFIRMED = "terkonfirmasi"
        const val STATUS_DONE = "selesai"
        const val STATUS_WAITING_FOR_CONTINUE_CONFIRMATION = "menunggu konfirmasi konsultasi lanjutan"
    }

    private val db = FirebaseFirestore.getInstance()
    private val listConsultations = MutableLiveData<ArrayList<Consultation>>()

    fun loadConsultations(){
        db.collection("consultations")
            .whereEqualTo("status", STATUS_WAITING_FOR_CONFIRMATION)
            .orderBy("created_at", Query.Direction.ASCENDING)
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.w(this::class.java.simpleName, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val listItems = ArrayList<Consultation>()
                for (doc in value) {
                    val consultation = Consultation(
                        id = doc.id,
                        userName = doc.data["user_name"] as String,
                        userId = doc.data["user_id"] as String,
                        problem = doc.data["problem"] as String,
                        effort = doc.data["effort"] as String,
                        obstacle = doc.data["obstacle"] as String,
                        status = doc.data["status"] as String,
                        timeRequest = doc.data["time_request"] as String,
                        genderRequest = doc.data["gender_request"] as String,
                        createdAt = doc.data["created_at"] as Timestamp,
                        timeAccepted = doc.data["time_accepted"] as Timestamp?,
                        counselorId = doc.data["counselor_id"] as String?,
                        counselorName = doc.data["counselor_name"] as String?,
                    )
                    listItems.add(consultation)
                }
                listConsultations.postValue(listItems)
            }
    }

    fun getConsultations() : LiveData<ArrayList<Consultation>> {
        return listConsultations
    }
}