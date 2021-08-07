package com.example.konsl.admin.ui.articles.create

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.konsl.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_article.*
import kotlinx.android.synthetic.main.dialog_choose_article_tag.*
import java.io.IOException
import java.util.*


class CreateArticleActivity : AppCompatActivity() {
    // Uri indicates, where the image will be picked from
    private var filePath: Uri? = null

    // request code
    private val PICK_IMAGE_REQUEST = 22
    private val TAG = this.javaClass.simpleName

    private lateinit var chooseTagDialog: AlertDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_article)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        editorSetup()

        supportActionBar?.let {
            it.title = getString(R.string.create_article)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
                imgThumbnail.visibility = View.VISIBLE
                btnUploadPhoto.text = getString(R.string.change_thumbnail)
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_article, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menuCreateArticle){
            if(isValid()){
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_article_tag, null)
                val mBuilder = AlertDialog.Builder(this)
                        .setView(mDialogView)
                        .setTitle(getString(R.string.choose_tag_article))
                        .setPositiveButton(R.string.publish) { _, _ ->
                            if(chooseTagDialog.rgArticleTag.checkedRadioButtonId != -1){
                                val rbChecked: RadioButton = chooseTagDialog.findViewById(chooseTagDialog.rgArticleTag.checkedRadioButtonId)
                                progressDialog()?.show()
                                uploadThumbnailToStorage(rbChecked.text.toString())
                            } else {
                                rbTutorial.error = getString(R.string.error_required)
                            }
                        }
                        .setNegativeButton(R.string.cancel) { _, _ ->
                            chooseTagDialog.cancel()
                        }
                chooseTagDialog = mBuilder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createArticle(tag: String, imageUrl: String){
        val data = hashMapOf(
                "title" to etTitle.text.toString(),
                "content" to editor.html.toString(),
                "tag" to tag.toLowerCase(Locale.ENGLISH),
                "created_at" to Timestamp.now(),
                "thumbnail_url" to imageUrl
        )

        db.collection("articles")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, getString(R.string.create_article_success), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                    progressDialog()?.dismiss()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, getString(R.string.create_article_failed), Toast.LENGTH_SHORT).show()
                    progressDialog()?.dismiss()
                    Log.w(TAG, "Error adding document", e)
                }
    }

    private fun uploadThumbnailToStorage(tag: String){
        val ref = storageReference.child("images/${UUID.randomUUID()}")

        if(filePath != null){
            ref.putFile(filePath!!)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        ref.downloadUrl
                    }
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            createArticle(tag, task.result.toString())
                            progressDialog()?.dismiss()
                        }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        progressDialog()?.dismiss()
                    }
        }

    }

    private fun isValid(): Boolean{
        var isValid = true
        if(etTitle.text.toString().isEmpty()
                && filePath == null
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
}