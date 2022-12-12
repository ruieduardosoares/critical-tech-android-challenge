package com.topnews.ui.topnews.adapter

import androidx.recyclerview.widget.DiffUtil
import com.topnews.domain.Article

class ArticleComparator : DiffUtil.ItemCallback<Article>() {

    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        // Articles dont have an id :(
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}
