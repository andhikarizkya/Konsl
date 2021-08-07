package com.example.konsl.user.ui.profile.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.konsl.R
import com.example.konsl.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_profile.etAddress
import kotlinx.android.synthetic.main.activity_edit_profile.etHobby
import kotlinx.android.synthetic.main.activity_edit_profile.etName
import kotlinx.android.synthetic.main.activity_edit_profile.etPhoneNumber

class EditProfileActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_USER = "extra_user"
    }
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: User
    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        intent.getParcelableExtra<User>(EXTRA_USER)?.let {
            etName.setText(it.name)
            etPhoneNumber.setText(it.phoneNumber)
            etHobby.setText(it.hobby)
            etAddress.setText(it.address)
            user = it
        }

        supportActionBar?.let {
            it.title = getString(R.string.edit_profile)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun saveChanges(view: View) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(etName.text.toString())
            .build()
        mAuth.currentUser?.updateProfile(profileUpdates)

        val updates = hashMapOf<String, Any>(
            "name" to etName.text.toString(),
            "phone_number" to etPhoneNumber.text.toString(),
            "hobby" to etHobby.text.toString(),
            "address" to etAddress.text.toString()
        )

        db.collection("users")
            .document(user.id)
            .update(updates)
            .addOnCompleteListener {
                Toast.makeText(this, getString(R.string.save_changes_success), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                Toast.makeText(this, getString(R.string.save_changes_failed), Toast.LENGTH_SHORT).show()
            }
    }
}