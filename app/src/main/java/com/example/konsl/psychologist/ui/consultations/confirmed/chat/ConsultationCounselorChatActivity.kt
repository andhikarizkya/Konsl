package com.example.konsl.psychologist.ui.consultations.confirmed.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.konsl.R
import com.example.konsl.adapter.ConsultationChatAdapter
import com.example.konsl.model.Consultation
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_consultation_counselor_chat.*

class ConsultationCounselorChatActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_CONSULTATION = "extra_consultation"
    }

    private lateinit var viewModel: ConsultationCounselorChatViewModel
    private lateinit var adapter: ConsultationChatAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var consultation: Consultation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation_counselor_chat)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        consultation = intent.getParcelableExtra(EXTRA_CONSULTATION)

        adapter = ConsultationChatAdapter(mAuth.uid!!)
        adapter.notifyDataSetChanged()

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        rvChatCounselor.layoutManager = linearLayoutManager
        rvChatCounselor.adapter = adapter

        viewModel = ViewModelProvider(this).get(ConsultationCounselorChatViewModel::class.java)
        consultation?.let { viewModel.loadMessages(it.id) }

        viewModel.getMessages().observe(this, Observer {messages ->
            messages?.let {
                rvChatCounselor.visibility = View.VISIBLE
                adapter.setData(it)
            }
        })

        supportActionBar?.let {
            it.title = consultation?.userName
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun sendMessage(view: View) {
        db.collection("consultations")
            .document(consultation!!.id)
            .collection("messages")
            .add(mapOf(
                "message" to etMessage.text.toString(),
                "sender_id" to mAuth.uid!!,
                "created_at" to Timestamp.now()
            ))
        etMessage.text?.clear()
    }
}