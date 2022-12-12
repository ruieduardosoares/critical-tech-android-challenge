package com.topnews.ui.topnews.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.topnews.databinding.ItemArticleBinding
import com.topnews.domain.Article

class ArticleAdapter constructor(
    articleComparator: ArticleComparator,
    private val onArticleClick: (Article) -> Unit,
) : PagingDataAdapter<Article, ArticleViewHolder>(articleComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemArticleBinding.inflate(layoutInflater, parent, false)
        return ArticleViewHolder(binding, onArticleClick)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}
