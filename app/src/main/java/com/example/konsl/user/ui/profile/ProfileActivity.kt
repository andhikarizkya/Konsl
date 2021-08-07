package com.example.konsl.user.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.konsl.R
import com.example.konsl.model.User
import com.example.konsl.user.ui.profile.edit.EditProfileActivity
import com.example.konsl.user.ui.profile.edit.EditProfileActivity.Companion.EXTRA_USER
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.item_consultation.view.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        mAuth.uid?.let { viewModel.loadUser(it) }

        viewModel.getUser().observe(this, {
            it?.let {
                tvName.text = it.name
                tvEmail.text = mAuth.currentUser?.email
                tvPhoneNumber.text = it.phoneNumber
                tvHobby.text = it.hobby
                tvAddress.text = it.address
                Picasso.get().load(R.drawable.dummy_profile)
                    .into(imgProfile)
                user = it
            }
        })

        supportActionBar?.let {
            it.title = getString(R.string.profile)
            it.setDisplayHomeAsUpEnabled(true)
            it.elevation = 0f
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun editProfile(view: View) {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra(EXTRA_USER, user)
        startActivity(intent)
    }
}