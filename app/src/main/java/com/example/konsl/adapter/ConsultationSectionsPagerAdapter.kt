package com.example.konsl.adapter

import android.content.Context
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.konsl.R
import com.example.konsl.psychologist.ui.consultations.confirmed.ConsultationConfirmedFragment
import com.example.konsl.psychologist.ui.consultations.request.ConsultationRequestFragment

class ConsultationSectionsPagerAdapter(private val mContext: Context, fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    @StringRes
    private val TAB_TITLES = intArrayOf(R.string.title_confirmed, R.string.title_request)

    override fun getCount(): Int = TAB_TITLES.size

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position){
            0 -> fragment = ConsultationConfirmedFragment()
            1 -> fragment = ConsultationRequestFragment()
        }
        return fragment as Fragment
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence? {
        return mContext.resources.getString(TAB_TITLES[position])
    }
}