package com.example.konsl.user.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.konsl.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val userData = MutableLiveData<User>()

    fun loadUser(uid: String){
        db.collection("users")
            .whereEqualTo("auth_id", uid)
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.w(this::class.java.simpleName, "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (document in value) {
                    val user = User(
                        id = document.id,
                        name = document.data["name"] as String,
                        authId = document.data["auth_id"] as String,
                        role = document.data["role"] as String,
                        gender = document.data["gender"] as String,
                        hobby = document.data["hobby"] as String,
                        address = document.data["address"] as String,
                        birthPlace = document.data["birth_place"] as String,
                        birthDate = document.data["birth_date"] as String,
                        phoneNumber = document.data["phone_number"] as String,
                    )
                    userData.postValue(user)
                    Log.d(this::class.java.simpleName, "${document.id} => ${document.data}")
                }
            }
    }

    fun getUser() : LiveData<User> {
        return userData
    }
}