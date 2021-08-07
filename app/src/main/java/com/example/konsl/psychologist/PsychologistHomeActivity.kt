package com.example.konsl.psychologist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.konsl.LoginActivity
import com.example.konsl.R
import com.example.konsl.adapter.ConsultationSectionsPagerAdapter
import com.example.konsl.user.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_psychologist_home.*

class PsychologistHomeActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: PsychologistHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psychologist_home)

        mAuth = FirebaseAuth.getInstance()

        val consultationSectionsPagerAdapter = ConsultationSectionsPagerAdapter(this, supportFragmentManager)
        viewPagerConsultation.adapter = consultationSectionsPagerAdapter
        tabsConsultation.setupWithViewPager(viewPagerConsultation)

        viewModel = ViewModelProvider(this).get(PsychologistHomeViewModel::class.java)
        viewModel.loadBadgeNumbers()

        viewModel.getConsultationRequestCount().observe(this, Observer { count ->
            if(count > 0){
                tabsConsultation.getTabAt(1)?.orCreateBadge?.number = count
            }
        })

        supportActionBar?.elevation = 0f
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menuLogout){
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else if (item.itemId == R.id.menuProfile){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}