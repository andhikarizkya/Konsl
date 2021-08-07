package com.example.konsl

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.konsl.user.UserHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private val ROLE_USER = "user"
    private val ROLE_ADMIN = "admin"
    private val ROLE_PSYCHOLOGIST = "psychologist"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        supportActionBar?.let {
            it.title = getString(R.string.register)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun signIn(view: View) {
        finish()
    }

    fun register(view: View) {
        if(isValid()){
            btnRegister.isEnabled = false
            btnRegister.text = getString(R.string.wait_a_moment)

            mAuth.createUserWithEmailAndPassword(
                etEmail.text.toString(),
                etPassword.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        if(user != null){
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(etName.text.toString())
                                .build()
                            user.updateProfile(profileUpdates)
                            saveUser(user)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        btnRegister.isEnabled = true
                        btnRegister.text = getString(R.string.register)
                        Toast.makeText(this, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun isValid(): Boolean {
        var isValid: Boolean = true
        if(etName.text.toString().isEmpty()){
            etName.error = getString(R.string.error_required)
            isValid = false
        }
        if(rgGender.checkedRadioButtonId == -1){
            rbFemale.error = getString(R.string.error_required)
            isValid = false
        }
        if(etPhoneNumber.text.toString().isEmpty()){
            etPhoneNumber.error = getString(R.string.error_required)
            isValid = false
        }
        if(etBirthPlace.text.toString().isEmpty()){
            etBirthPlace.error = getString(R.string.error_required)
            isValid = false
        }
        if(etBirthDate.text.toString().isEmpty()){
            etBirthDate.error = getString(R.string.error_required)
            isValid = false
        }
        if(etHobby.text.toString().isEmpty()){
            etHobby.error = getString(R.string.error_required)
            isValid = false
        }
        if(etAddress.text.toString().isEmpty()){
            etAddress.error = getString(R.string.error_required)
            isValid = false
        }
        if(etPassword.text.toString().isEmpty()){
            etPassword.error = getString(R.string.error_required)
            isValid = false
        } else {
            if(etPassword.text.toString().length < 6){
                etPassword.error = getString(R.string.error_password_minimum)
                isValid = false
            }
        }
        if(etPasswordConfirmation.text.toString().isEmpty()){
            etPasswordConfirmation.error = getString(R.string.error_required)
            isValid = false
        } else {
            if(etPassword.text.toString() != etPasswordConfirmation.text.toString()){
                etPasswordConfirmation.error = getString(R.string.error_password_not_equsals)
                isValid = false
            }
        }

        return if(isValid){
            true
        } else {
            Toast.makeText(
                    this,
                    getString(R.string.please_input_correctly),
                    Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    private val myCalendar: Calendar = Calendar.getInstance()

    var date =
        OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            updateBirthDateLabel()
        }

    fun showDatePicker(view: View) {
        DatePickerDialog(
            this, date, myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun updateBirthDateLabel(){
        val myFormat = "dd/MM/yyyy" //In which you need put here

        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        etBirthDate.setText(sdf.format(myCalendar.time))
    }

    private fun saveUser(authUser: FirebaseUser){
        val db = FirebaseFirestore.getInstance()

        val user: MutableMap<String, Any> = HashMap()
        user["auth_id"] = authUser.uid
        user["name"] = etName.text.toString()
        val genderChecked: RadioButton = findViewById(rgGender.checkedRadioButtonId)
        user["gender"] = genderChecked.text.toString()
        user["phone_number"] = etPhoneNumber.text.toString()
        user["birth_place"] = etBirthPlace.text.toString()
        user["birth_date"] = etBirthDate.text.toString()
        user["hobby"] = etHobby.text.toString()
        user["address"] = etAddress.text.toString()
        user["role"] = ROLE_USER

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Log.d(this::class.java.simpleName, "DocumentSnapshot added with ID: " + documentReference.id)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                btnRegister.isEnabled = true
                btnRegister.text = getString(R.string.register)
            }
    }
}