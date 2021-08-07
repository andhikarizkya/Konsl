package com.example.konsl.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.konsl.R
import com.example.konsl.model.Article
import com.example.konsl.user.ui.educations.detail.ArticleDetailActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_tutorial.view.*

class TutorialAdapter: RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder>() {
    private val mData = ArrayList<Article>()

    fun setData(items: ArrayList<Article>){
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    inner class TutorialViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(articleItem: Article){
            with(itemView){
                Picasso.get().load(articleItem.thumbnailUrl)
                        .placeholder(R.drawable.dummy)
                        .into(imgThumbnail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_tutorial, parent, false)
        return TutorialViewHolder(mView)
    }

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        val article = mData[position]
        holder.bind(article)

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, ArticleDetailActivity::class.java)
            intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE, article)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = mData.size
}