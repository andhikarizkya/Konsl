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
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleAdapter: RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val mData = ArrayList<Article>()

    fun setData(items: ArrayList<Article>){
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(articleItem: Article){
            with(itemView){
                tvTitle.text = articleItem.title
                Picasso.get().load(articleItem.thumbnailUrl)
                    .placeholder(R.drawable.dummy)
                    .into(imgThumbnail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
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