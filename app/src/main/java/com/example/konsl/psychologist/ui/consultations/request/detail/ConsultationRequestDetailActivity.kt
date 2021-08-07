package com.example.konsl.psychologist.ui.consultations.request.detail

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.konsl.R
import com.example.konsl.psychologist.PsychologistHomeActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_consultation_request_detail.*
import kotlinx.android.synthetic.main.dialog_accept_consultation_form.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ConsultationRequestDetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_REQUEST_ID = "extra_request_id"
        const val STATUS_CONFIRMED = "terkonfirmasi"
    }
    private lateinit var viewModel: ConsultationRequestDetailViewModel
    private lateinit var chooseTimeDialog: AlertDialog
    private var userId: String? = null
    private var requestId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation_request_detail)

        userId = intent.getStringExtra(EXTRA_USER_ID)
        requestId = intent.getStringExtra(EXTRA_REQUEST_ID)

        viewModel = ViewModelProvider(this).get(ConsultationRequestDetailViewModel::class.java)
        userId?.let { viewModel.loadUser(it) }
        requestId?.let { viewModel.loadConsultation(it) }

        viewModel.getUser().observe(this, Observer { result ->
            tvName.text = result.name
            tvGender.text = result.gender
            tvBirthDate.text = result.birthDate
            tvHobby.text = result.hobby
        })

        viewModel.getConsultation().observe(this, Observer { result ->
            tvProblem.text = result.problem
            tvEffort.text = result.effort
            tvObstacle.text = result.obstacle
            tvCounselorGender.text = result.genderRequest
            tvTimeRequest.text = result.timeRequest
        })

        supportActionBar?.let {
            it.title = getString(R.string.title_request)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun acceptConsultation(view: View) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_accept_consultation_form, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle(getString(R.string.choose_time))
                .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener{ _, _ ->
                    val dateAccepted = chooseTimeDialog.etDateAccepted.text.toString()
                    val timeAccepted = chooseTimeDialog.etTimeAccepted.text.toString()
                    saveConsultationAccepted(dateAccepted, timeAccepted)
                })
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener{ _, _ ->
                    chooseTimeDialog.cancel()
                })
        chooseTimeDialog = mBuilder.show()
    }

    private fun saveConsultationAccepted(dateAccepted: String, timeAccepted: String) {
        val db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()

        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = formatter.parse("$dateAccepted $timeAccepted")
        val updateValue = mapOf(
                "time_accepted" to Timestamp(date!!),
                "status" to STATUS_CONFIRMED,
                "counselor_id" to mAuth.uid,
                "counselor_name" to mAuth.currentUser?.displayName,
        )

        db.collection("consultations").document(requestId!!)
                .update(updateValue)
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.consultation_accepted), Toast.LENGTH_SHORT).show()
                    finish()
                    Log.d(ConsultationRequestDetailActivity::class.java.simpleName, "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, getString(R.string.accepting_consultation_failed), Toast.LENGTH_SHORT).show()
                    Log.w(ConsultationRequestDetailActivity::class.java.simpleName, "Error updating document", e)
                }

    }

    fun cancel(view: View) {
        finish()
    }

    private val myCalendar: Calendar = Calendar.getInstance()

    private var date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateDateAcceptedLabel()
            }

    fun showDatePicker(view: View) {
        DatePickerDialog(
                this, date, myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun updateDateAcceptedLabel(){
        val myFormat = "dd/MM/yyyy" //In which you need put here

        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        chooseTimeDialog.etDateAccepted.setText(sdf.format(myCalendar.time))
    }

    private var time = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        myCalendar[Calendar.HOUR_OF_DAY] = hour
        myCalendar[Calendar.MINUTE] = minute
        updateTimeAcceptedLabel()
    }

    fun showTimePicker(view: View){
        TimePickerDialog(
                this, time,
                myCalendar[Calendar.HOUR_OF_DAY],
                myCalendar[Calendar.MINUTE],
                true
        ).show()
    }

    private fun updateTimeAcceptedLabel(){
        val myFormat = "HH:mm"

        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        chooseTimeDialog.etTimeAccepted.setText(sdf.format(myCalendar.time))
    }
}