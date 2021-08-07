package com.example.konsl.user.ui.educations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.example.konsl.R
import com.example.konsl.adapter.ArticleAdapter
import kotlinx.android.synthetic.main.fragment_educations.*

class EducationsFragment : Fragment() {

    private lateinit var educationsViewModel: EducationsViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var skeleton: RecyclerViewSkeletonScreen

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_educations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        articleAdapter = ArticleAdapter()
        articleAdapter.notifyDataSetChanged()

        rvArticles.layoutManager = LinearLayoutManager(context)
        rvArticles.adapter = articleAdapter

        skeleton = Skeleton.bind(rvArticles)
                .adapter(articleAdapter)
                .load(R.layout.skeleton_item_article)
                .shimmer(true)
                .duration(500)
                .show()

        educationsViewModel = ViewModelProvider(this).get(EducationsViewModel::class.java)
        educationsViewModel.setArticles()

        educationsViewModel.getArticles().observe(viewLifecycleOwner, Observer { articles ->
            articles?.let {
                skeleton.hide()
                articleAdapter.setData(it)
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }
}