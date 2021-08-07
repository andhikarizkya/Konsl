package com.example.konsl.admin.ui.articles.edit

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.konsl.R
import com.example.konsl.model.Article
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_article.*
import java.io.IOException
import java.util.*

class EditArticleActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_ARTICLE = "extra_article"
    }
    private var article: Article? = null
    private lateinit var db: FirebaseFirestore

    private val PICK_IMAGE_REQUEST = 22
    private val TAG = this.javaClass.simpleName

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_article)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        article = intent.getParcelableExtra(EXTRA_ARTICLE)

        editorSetup()

        article?.let {
            etTitle.setText(it.title)
            Picasso.get().load(it.thumbnailUrl)
                .placeholder(R.drawable.dummy)
                .into(imgThumbnail)
            editor.html = it.content
        }

        supportActionBar?.let {
            it.title = getString(R.string.edit_article)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun uploadPhoto(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver, filePath
                )
                imgThumbnail.setImageBitmap(bitmap)
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_article, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menuSaveChanges){
            updateArticle()
        } else if(item.itemId == R.id.menuDelete) {
            deleteArticle()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateArticle(){
        if(isValid()){
            progressDialog()?.show()
            article?.let{
                db.collection("articles").document(it.id)
                        .update(mapOf(
                                "title" to etTitle.text.toString(),
                                "content" to editor.html.toString(),
                                "created_at" to Timestamp.now(),
                        ))
                        .addOnSuccessListener {
                            if(filePath != null){
                                val ref = storageReference.child("images/${UUID.randomUUID()}")

                                ref.putFile(filePath!!)
                                        .continueWithTask { task ->
                                            if (!task.isSuccessful) {
                                                task.exception?.let { err ->
                                                    throw err
                                                }
                                            }
                                            ref.downloadUrl
                                        }
                                        .addOnCompleteListener { task ->
                                            if(task.isSuccessful){
                                                updateArticleThumbnail(task.result.toString())
                                            }
                                        }
                                        .addOnFailureListener { error ->
                                            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                                            progressDialog()?.dismiss()
                                        }
                            } else {
                                Toast.makeText(this, getString(R.string.update_article_success), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, getString(R.string.update_article_failed), Toast.LENGTH_SHORT).show()
                            Log.w(TAG, "Error updating document", e)
                        }
            }
        }
    }

    private fun updateArticleThumbnail(thumbnailUrl: String){
        article?.let{
            db.collection("articles").document(it.id)
                    .update("thumbnail_url", thumbnailUrl)
                    .addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.update_article_success), Toast.LENGTH_SHORT).show()
                        progressDialog()?.dismiss()
                        finish()
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        progressDialog()?.dismiss()
                        Toast.makeText(this, getString(R.string.update_article_failed), Toast.LENGTH_SHORT).show()
                        Log.w(TAG, "Error updating document", e)
                    }
        }
    }

    private fun isValid():Boolean{
        var isValid = true
        if(etTitle.text.toString().isEmpty()
                && editor.html == null){
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }

    private fun progressDialog(): AlertDialog? {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        return AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle(getString(R.string.wait_a_moment))
                .setCancelable(false)
                .create()
    }

    private fun deleteArticle(){
        article?.let{
            db.collection("articles").document(it.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.delete_article_success), Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, getString(R.string.delete_article_failed), Toast.LENGTH_SHORT).show()
                        Log.w(TAG, "Error deleting document", e)
                    }
        }
    }

    private fun editorSetup(){
        editor.setPadding(16, 16, 16, 16)
        editor.setEditorFontSize(18)
        editor.setPlaceholder(getString(R.string.content_article))

        actionUndo.setOnClickListener {
            editor.undo()
        }
        actionRedo.setOnClickListener {
            editor.redo()
        }
        actionBold.setOnClickListener {
            editor.setBold()
        }
        actionItalic.setOnClickListener {
            editor.setItalic()
        }
        actionUnderline.setOnClickListener {
            editor.setUnderline()
        }
        actionStrikethrough.setOnClickListener {
            editor.setStrikeThrough()
        }
        actionHeading1.setOnClickListener {
            editor.setHeading(1)
        }
        actionHeading2.setOnClickListener {
            editor.setHeading(2)
        }
        actionHeading3.setOnClickListener {
            editor.setHeading(3)
        }
        actionIndent.setOnClickListener {
            editor.setIndent()
        }
        actionOutdent.setOnClickListener {
            editor.setOutdent()
        }
        actionAlignLeft.setOnClickListener {
            editor.setAlignLeft()
        }
        actionAlignCenter.setOnClickListener {
            editor.setAlignCenter()
        }
        actionAlignRight.setOnClickListener {
            editor.setAlignRight()
        }
        actionInsertBullets.setOnClickListener {
            editor.setBullets()
        }
        actionInsertNumbers.setOnClickListener {
            editor.setNumbers()
        }
    }
}