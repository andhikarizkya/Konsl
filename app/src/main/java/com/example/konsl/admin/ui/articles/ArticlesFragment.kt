package com.example.konsl.admin.ui.articles

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.example.konsl.LoginActivity
import com.example.konsl.R
import com.example.konsl.adapter.ArticleAdminAdapter
import com.example.konsl.admin.ui.articles.create.CreateArticleActivity
import com.example.konsl.user.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_articles.*

class ArticlesFragment : Fragment() {

    companion object {
        fun newInstance() = ArticlesFragment()
    }

    private lateinit var viewModel: ArticlesViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var skeleton: RecyclerViewSkeletonScreen
    private lateinit var articleAdapter: ArticleAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_articles, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        articleAdapter = ArticleAdminAdapter()
        articleAdapter.notifyDataSetChanged()

        rvArticles.layoutManager = LinearLayoutManager(context)
        rvArticles.adapter = articleAdapter

        viewModel = ViewModelProvider(this).get(ArticlesViewModel::class.java)
        viewModel.loadArticles()

        skeleton = Skeleton.bind(rvArticles)
            .adapter(articleAdapter)
            .load(R.layout.skeleton_item_article)
            .shimmer(true)
            .duration(500)
            .show()

        viewModel.getArticles().observe(viewLifecycleOwner,{
            it?.let {
                skeleton.hide()
                articleAdapter.setData(it)
            }
        })

        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.article)
        setHasOptionsMenu(true)

        fabCreateArticle.setOnClickListener{
            val intent = Intent(context, CreateArticleActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menuLogout){
            mAuth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else if (item.itemId == R.id.menuProfile){
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}