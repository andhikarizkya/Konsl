package com.example.konsl.user.ui.consultations.request

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.example.konsl.R
import com.example.konsl.user.UserHomeActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_request_consultation.*

class RequestConsultationActivity : AppCompatActivity() {
    companion object {
        const val STATUS_WAITING_FOR_CONFIRMATION = "menunggu konfirmasi"
        const val STATUS_CONFIRMED = "terkonfirmasi"
        const val STATUS_DONE = "selesai"
        const val STATUS_WAITING_FOR_CONTINUE_CONFIRMATION = "menunggu konfirmasi konsultasi lanjutan"
    }

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_consultation)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        supportActionBar?.let {
            it.title = getString(R.string.consultation_request)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun isValid(): Boolean{
        var isValid: Boolean = true
        if(etProblem.text.toString().isEmpty()){
            etProblem.error = getString(R.string.error_required)
            isValid = false
        }
        if(etEffort.text.toString().isEmpty()){
            etEffort.error = getString(R.string.error_required)
            isValid = false
        }
        if(etObstacle.text.toString().isEmpty()){
            etObstacle.error = getString(R.string.error_required)
            isValid = false
        }
        if(rgGender.checkedRadioButtonId == -1){
            rbNotSpecified.error = getString(R.string.error_required)
            isValid = false
        }
        if(etTimeRequest.text.toString().isEmpty()){
            etTimeRequest.error = getString(R.string.error_required)
            isValid = false
        }

        return if(isValid){
            true
        } else {
            Toast.makeText(this, getString(R.string.please_input_correctly), Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun registerConsultation(view: View) {
        if(isValid()){
            btnRegisteringConsultation.isEnabled = false
            btnRegisteringConsultation.text = getString(R.string.wait_a_moment)

            val genderChecked: RadioButton = findViewById(rgGender.checkedRadioButtonId)
            val consultation = hashMapOf(
                "problem" to etProblem.text.toString(),
                "effort" to etEffort.text.toString(),
                "obstacle" to etObstacle.text.toString(),
                "time_request" to etTimeRequest.text.toString(),
                "gender_request" to genderChecked.text.toString(),
                "status" to STATUS_WAITING_FOR_CONFIRMATION,
                "user_id" to mAuth.uid,
                "user_name" to mAuth.currentUser?.displayName,
                "created_at" to Timestamp.now()
            )

            db.collection("consultations")
                .add(consultation)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, getString(R.string.requesting_consultation_success), Toast.LENGTH_SHORT).show()
                    finish()
                    Log.d(this::class.java.simpleName, "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    btnRegisteringConsultation.isEnabled = true
                    btnRegisteringConsultation.text = getString(R.string.registering_consultation)
                    Toast.makeText(this, getString(R.string.requesting_consultation_failed), Toast.LENGTH_SHORT).show()
                    Log.w(this::class.java.simpleName, "Error adding document", e)
                }
        }
    }

    fun cancelConsultation(view: View) {
        finish()
    }
}