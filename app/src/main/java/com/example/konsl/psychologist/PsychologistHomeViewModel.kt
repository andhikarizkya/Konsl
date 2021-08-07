package com.example.konsl.psychologist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.konsl.model.Consultation
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PsychologistHomeViewModel: ViewModel() {
    companion object {
        const val STATUS_WAITING_FOR_CONFIRMATION = "menunggu konfirmasi"
        const val STATUS_CONFIRMED = "terkonfirmasi"
        const val STATUS_DONE = "selesai"
        const val STATUS_WAITING_FOR_CONTINUE_CONFIRMATION = "menunggu konfirmasi konsultasi lanjutan"
    }

    private val db = FirebaseFirestore.getInstance()
    private val consultationRequestCount = MutableLiveData<Int>()

    fun loadBadgeNumbers(){
        db.collection("consultations")
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.w(this::class.java.simpleName, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val listItems = ArrayList<String>()
                for (doc in value) {
                    val status = doc.data["status"] as String
                    listItems.add(status)
                }
                val reqCount = listItems.filter{item -> item == STATUS_WAITING_FOR_CONFIRMATION}.size
                val conCount = listItems.filter{item -> item == STATUS_CONFIRMED}.size
                consultationRequestCount.postValue(reqCount)
            }
    }

    fun getConsultationRequestCount() : LiveData<Int> {
        return consultationRequestCount
    }

}