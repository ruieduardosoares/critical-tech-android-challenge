package com.topnews.ui.topnews.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.topnews.convertDpToPixel
import com.topnews.databinding.ItemArticleBinding
import com.topnews.domain.Article

class ArticleViewHolder(
    private val binding: ItemArticleBinding,
    private val onClick: (Article) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var imageLoadDisposable: Disposable? = null

    fun bind(article: Article?) {
        article?.let {
            setupItemClickListener(article)
            renderImage(article)
            renderTitle(article)
        }
    }

    private fun setupItemClickListener(article: Article) {
        binding.root.setOnClickListener { onClick(article) }
    }

    private fun renderImage(article: Article) {
        imageLoadDisposable?.dispose()

        val context = itemView.context
        val imageLoader = context.imageLoader

        val request = ImageRequest.Builder(context)
            .data(article.urlToImage)
            .crossfade(true)
            .fallback(android.R.drawable.ic_delete)
            .error(android.R.drawable.ic_delete)
            .size(
                convertDpToPixel(140f, itemView.context),
                convertDpToPixel(80f, itemView.context)
            )
            .target(binding.articleImage)
            .build()

        imageLoadDisposable = imageLoader.enqueue(request)
    }

    private fun renderTitle(article: Article) {
        binding.articleName.text = article.title
    }
}
