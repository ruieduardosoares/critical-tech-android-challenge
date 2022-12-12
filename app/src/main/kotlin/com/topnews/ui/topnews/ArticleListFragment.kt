package com.topnews.ui.topnews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.topnews.NewsSourceConfig
import com.topnews.R
import com.topnews.convertDpToPixel
import com.topnews.databinding.FragmentArticleListBinding
import com.topnews.domain.Article
import com.topnews.ui.article.ArticleFragment
import com.topnews.ui.topnews.adapter.ArticleAdapter
import com.topnews.ui.topnews.adapter.ArticleComparator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ArticleListFragment : Fragment() {

    private val viewModel: ArticleListViewModel by viewModels()

    @Inject
    lateinit var newsSourceConfig: NewsSourceConfig

    private val onArticleClicked: (Article) -> Unit = { article ->
        findNavController().navigate(R.id.action_topNewsFragment_to_articleFragment, ArticleFragment.toBundle(article))
    }

    private val articleAdapter = ArticleAdapter(ArticleComparator(), onArticleClicked)

    private var binding: FragmentArticleListBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentArticleListBinding.inflate(layoutInflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView(view)
        setupToolbar()
        setupArticlesRecyclerView()
        setupRetryClickListener()
        setupStateAndDataCollection()
    }

    private fun bindView(view: View) {
        binding = FragmentArticleListBinding.bind(view)
    }

    private fun setupToolbar() {
        binding?.toolbar?.isVisible = true
        binding?.toolbar?.title = newsSourceConfig.name
    }

    private fun setupArticlesRecyclerView() {
        val context = requireContext()

        val recyclerView = binding?.recyclerView

        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = linearLayoutManager


        val itemDecoration = DividerItemDecoration(context, linearLayoutManager.orientation)
        val insetDivider = loadInsetDivider(context)
        itemDecoration.setDrawable(insetDivider)
        recyclerView?.addItemDecoration(itemDecoration)

        recyclerView?.adapter = articleAdapter
    }

    private fun loadInsetDivider(context: Context): InsetDrawable {
        val attrs = intArrayOf(android.R.attr.listDivider)
        val a: TypedArray = requireActivity().obtainStyledAttributes(attrs)
        val divider = a.getDrawable(0)
        val inset = convertDpToPixel(170f, context)
        val insetDivider = InsetDrawable(divider, inset, 0, 0, 0)
        a.recycle()
        return insetDivider
    }

    private fun setupRetryClickListener() {
        binding?.retryButton?.setOnClickListener { articleAdapter.retry() }
    }

    private fun setupStateAndDataCollection() {
        lifecycleScope.launch {
            articleAdapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Loading -> renderLoadingState()
                    is LoadState.Error -> renderErrorState(loadStates)
                    else -> renderResultsLoadedState()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.articleListPagingFlow.collectLatest { articlesPaged ->
                articleAdapter.submitData(articlesPaged)
            }
        }
    }

    private fun renderLoadingState() {
        binding?.recyclerView?.isVisible = false
        binding?.progressBar?.isVisible = true
        binding?.retryButton?.isVisible = false
        binding?.errorMsg?.isVisible = false
    }

    private fun renderErrorState(loadStates: CombinedLoadStates) {
        binding?.recyclerView?.isVisible = false
        binding?.progressBar?.isVisible = false
        binding?.retryButton?.isVisible = true
        binding?.errorMsg?.isVisible = true
        binding?.errorMsg?.text = (loadStates.refresh as LoadState.Error).error.localizedMessage
    }

    private fun renderResultsLoadedState() {
        binding?.recyclerView?.isVisible = true
        binding?.progressBar?.isVisible = false
        binding?.retryButton?.isVisible = false
        binding?.errorMsg?.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindView()
    }

    private fun unbindView() {
        binding = null
    }
}
