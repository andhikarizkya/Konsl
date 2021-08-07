package com.example.konsl.psychologist.ui.consultations.request.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.konsl.model.Consultation
import com.example.konsl.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class ConsultationRequestDetailViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val user = MutableLiveData<User>()
    private val consultation = MutableLiveData<Consultation>()

    fun loadUser(uid: String){
        db.collection("users")
            .whereEqualTo("auth_id", uid)
            .limit(1)
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.w(this::class.java.simpleName, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val listItems = ArrayList<User>()
                for (doc in value) {
                    val item = User(
                            id = doc.id,
                            name = doc.data["name"] as String,
                            authId = doc.data["auth_id"] as String,
                            role = doc.data["role"] as String,
                            gender = doc.data["gender"] as String,
                            hobby = doc.data["hobby"] as String,
                            address = doc.data["address"] as String,
                            birthPlace = doc.data["birth_place"] as String,
                            birthDate = doc.data["birth_date"] as String,
                            phoneNumber = doc.data["phone_number"] as String,
                    )
                    listItems.add(item)
                    Log.d(this::class.java.simpleName, "${doc.id} => ${doc.data}")
                }
                user.postValue(listItems[0])
            }
    }

    fun loadConsultation(id: String){
        db.collection("consultations")
            .document(id)
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.w(this::class.java.simpleName, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val item = Consultation(
                        id = value.id,
                        userName = value.data!!["user_name"] as String,
                        userId = value.data!!["user_id"] as String,
                        problem = value.data!!["problem"] as String,
                        effort = value.data!!["effort"] as String,
                        obstacle = value.data!!["obstacle"] as String,
                        status = value.data!!["status"] as String,
                        timeRequest = value.data!!["time_request"] as String,
                        genderRequest = value.data!!["gender_request"] as String,
                        createdAt = value.data!!["created_at"] as Timestamp,
                        timeAccepted = value.data!!["time_accepted"] as Timestamp?,
                        counselorId = value.data!!["counselor_id"] as String?,
                        counselorName = value.data!!["counselor_name"] as String?,
                )

                consultation.postValue(item)
            }
    }

    fun getUser(): LiveData<User>{
        return user
    }

    fun getConsultation(): LiveData<Consultation>{
        return consultation
    }
}