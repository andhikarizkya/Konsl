package com.example.konsl.psychologist.ui.consultations.confirmed

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.konsl.R
import com.example.konsl.adapter.ConsultationCounselorAdapter
import kotlinx.android.synthetic.main.fragment_consultation_confirmed.*

class ConsultationConfirmedFragment : Fragment() {
    private lateinit var viewModel: ConsultationConfirmedViewModel
    private lateinit var adapter: ConsultationCounselorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consultation_confirmed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ConsultationCounselorAdapter()
        adapter.notifyDataSetChanged()

        rvConsultationConfirmed.layoutManager = LinearLayoutManager(context)
        rvConsultationConfirmed.adapter = adapter

        viewModel = ViewModelProvider(this).get(ConsultationConfirmedViewModel::class.java)
        viewModel.loadConsultations()

        viewModel.getConsultations().observe(viewLifecycleOwner, Observer {consultations ->
            consultations?.let {
                progressBarConsultations.visibility = View.INVISIBLE
                if(it.isNotEmpty()){
                    rvConsultationConfirmed.visibility = View.VISIBLE
                    layoutNoConsultation.visibility = View.INVISIBLE
                    adapter.setData(it)
                } else {
                    layoutNoConsultation.visibility = View.VISIBLE
                    rvConsultationConfirmed.visibility = View.INVISIBLE
                }
            }
        })
    }

}