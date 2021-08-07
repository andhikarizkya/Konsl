package com.example.konsl.user.ui.educations.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.konsl.R
import com.example.konsl.model.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_article_detail.*

class ArticleDetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_ARTICLE = "extra_article"
    }
    private lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        article = intent.getParcelableExtra<Article>(EXTRA_ARTICLE) as Article
        tvTitle.text = article.title
        Picasso.get().load(article.thumbnailUrl)
            .placeholder(R.drawable.dummy)
            .into(imgThumbnail)
        article.content?.let {
            wvContent.loadDataWithBaseURL(null, it, "text/html", "utf-8", null)
        }

        supportActionBar?.let {
            it.title = getString(R.string.article)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}