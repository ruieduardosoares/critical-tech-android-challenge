package com.topnews.ui.topnews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.topnews.ui.topnews.adapter.paging.ArticlePagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel @Inject constructor(private val articlePagingSource: ArticlePagingSource) : ViewModel() {

    val articleListPagingFlow = Pager(PagingConfig(pageSize = 20)) { articlePagingSource }.flow.cachedIn(viewModelScope)
}
