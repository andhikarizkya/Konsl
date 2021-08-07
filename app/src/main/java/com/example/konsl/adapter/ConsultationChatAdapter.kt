package com.example.konsl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.konsl.R
import com.example.konsl.model.Message
import kotlinx.android.synthetic.main.item_message.view.*

class ConsultationChatAdapter(val uid: String): RecyclerView.Adapter<ConsultationChatAdapter.ConsultationChatViewHolder>() {
    private val mData = ArrayList<Message>()

    fun setData(items: ArrayList<Message>){
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    inner class ConsultationChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: Message){
            with(itemView){
                if(item.senderId == uid){
                    tvMessageSent.visibility = View.VISIBLE
                    tvMessageSent.text = item.message
                    tvMessageReceived.visibility = View.GONE
                } else {
                    tvMessageReceived.visibility = View.VISIBLE
                    tvMessageReceived.text = item.message
                    tvMessageSent.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationChatViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ConsultationChatViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ConsultationChatViewHolder, position: Int) {
        val message = mData[position]
        holder.bind(message)

        holder.itemView.setOnClickListener{
            //TODO
        }
    }

    override fun getItemCount(): Int = mData.size
}