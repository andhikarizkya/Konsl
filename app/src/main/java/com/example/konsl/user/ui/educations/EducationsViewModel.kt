package com.example.konsl.user.ui.educations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.konsl.model.Article
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class EducationsViewModel : ViewModel() {
    companion object{
        const val TYPE_EDUCATION = "edukasi"
    }

    private val db = FirebaseFirestore.getInstance()
    private val listArticles = MutableLiveData<ArrayList<Article>>()

    fun setArticles(){
        db.collection("articles")
            .whereEqualTo("tag", TYPE_EDUCATION)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    Log.w(this::class.java.simpleName, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val listItems = ArrayList<Article>()
                for (document in value) {
                    val article = Article(
                            id = document.id,
                            title = document.data["title"] as String,
                            thumbnailUrl = document.data["thumbnail_url"] as String,
                            content = document.data["content"] as String,
                    )
                    listItems.add(article)
                    Log.d(this::class.java.simpleName, "${document.id} => ${document.data}")
                }
                listArticles.postValue(listItems)
            }
    }

    fun getArticles() : LiveData<ArrayList<Article>> {
        return listArticles
    }
}