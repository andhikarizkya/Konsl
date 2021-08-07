package com.example.konsl.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.konsl.R
import com.example.konsl.admin.ui.articles.ArticlesFragment

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ArticlesFragment.newInstance())
                .commitNow()
        }
    }
}