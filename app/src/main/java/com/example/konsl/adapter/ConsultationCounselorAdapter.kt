package com.example.konsl.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.konsl.R
import com.example.konsl.model.Consultation
import com.example.konsl.psychologist.ui.consultations.confirmed.chat.ConsultationCounselorChatActivity
import com.example.konsl.psychologist.ui.consultations.request.detail.ConsultationRequestDetailActivity
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_consultation.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ConsultationCounselorAdapter: RecyclerView.Adapter<ConsultationCounselorAdapter.ConsultationRequestViewHolder>() {
    companion object {
        const val STATUS_WAITING_FOR_CONFIRMATION = "menunggu konfirmasi"
        const val STATUS_CONFIRMED = "terkonfirmasi"
        const val STATUS_DONE = "selesai"
        const val STATUS_WAITING_FOR_CONTINUE_CONFIRMATION = "menunggu konfirmasi konsultasi lanjutan"
    }

    private val mData = ArrayList<Consultation>()

    fun setData(items: ArrayList<Consultation>){
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    inner class ConsultationRequestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(consultationItem: Consultation){
            with(itemView){
                when(consultationItem.status){
                    STATUS_WAITING_FOR_CONFIRMATION -> {
                        tvTitle.text = consultationItem.userName
                        val localeByLanguageTag = Locale.forLanguageTag("id")
                        val timeMessages = TimeAgoMessages.Builder().withLocale(localeByLanguageTag).build()
                        tvInfo.text = resources.getString(R.string.requested_time, TimeAgo.using(consultationItem.createdAt.toDate().time, timeMessages))
                        Picasso.get().load(R.drawable.dummy_profile)
                            .into(imgThumbnail)
                    }
                    STATUS_CONFIRMED -> {
                        tvTitle.text = consultationItem.userName
                        val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy HH:mm", Locale.getDefault())
                        tvInfo.text = dateFormat.format(consultationItem.timeAccepted!!.toDate())
                        Picasso.get().load(R.drawable.dummy_profile)
                            .into(imgThumbnail)
                    }
                    //TODO
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationRequestViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_consultation, parent, false)
        return ConsultationRequestViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ConsultationRequestViewHolder, position: Int) {
        val consultation = mData[position]
        holder.bind(consultation)

        holder.itemView.setOnClickListener{
            when(consultation.status){
                STATUS_WAITING_FOR_CONFIRMATION -> {
                    val intent = Intent(holder.itemView.context, ConsultationRequestDetailActivity::class.java)
                    intent.putExtra(ConsultationRequestDetailActivity.EXTRA_REQUEST_ID, consultation.id)
                    intent.putExtra(ConsultationRequestDetailActivity.EXTRA_USER_ID, consultation.userId)
                    holder.itemView.context.startActivity(intent)
                }
                STATUS_CONFIRMED -> {
                    val intent = Intent(holder.itemView.context, ConsultationCounselorChatActivity::class.java)
                    intent.putExtra(ConsultationCounselorChatActivity.EXTRA_CONSULTATION, consultation)
                    holder.itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = mData.size
}