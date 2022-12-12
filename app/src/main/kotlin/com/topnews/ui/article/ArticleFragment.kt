package com.topnews.ui.article

import android.R
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import coil.imageLoader
import coil.request.ImageRequest
import com.topnews.convertDpToPixel
import com.topnews.databinding.FragmentArticleBinding
import com.topnews.domain.Article

class ArticleFragment : Fragment() {

    private var title: String? = null
    private var imageUrl: String? = null
    private var description: String? = null
    private var content: String? = null

    private var binding: FragmentArticleBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadArguments()
    }

    private fun loadArguments() {
        val requiredArguments = requireArguments()
        title = requiredArguments.getString(TITLE_EXTRA)
        imageUrl = requiredArguments.getString(IMAGE_URL_EXTRA)
        description = requiredArguments.getString(DESCRIPTION_EXTRA)
        content = requiredArguments.getString(CONTENT_EXTRA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentArticleBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView(view)
        renderArticleImage()
        renderArticleTitle()
        renderArticleDescription()
        renderArticleContent()
    }

    private fun bindView(view: View) {
        binding = FragmentArticleBinding.bind(view)
    }

    private fun renderArticleImage() {
        binding?.articleImage?.let {
            val context = requireContext()
            val imageLoader = context.imageLoader
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .fallback(R.drawable.ic_delete)
                .error(R.drawable.ic_delete)
                .size(
                    getScreenWidthInPixel(),
                    convertDpToPixel(200f, context)
                )
                .target(it)
                .build()
            imageLoader.enqueue(request)
        }
    }

    private fun getScreenWidthInPixel(): Int {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    private fun renderArticleTitle() {
        binding?.articleName?.text = title
    }

    private fun renderArticleDescription() {
        binding?.articleDescription?.text = description
    }

    private fun renderArticleContent() {
        binding?.articleContent?.text = content
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindView()
    }

    private fun unbindView() {
        binding = null
    }

    companion object {

        private const val TITLE_EXTRA = "title"
        private const val IMAGE_URL_EXTRA = "imageUrl"
        private const val DESCRIPTION_EXTRA = "description"
        private const val CONTENT_EXTRA = "content"

        fun toBundle(article: Article): Bundle =
            bundleOf(
                TITLE_EXTRA to article.title,
                IMAGE_URL_EXTRA to article.urlToImage,
                DESCRIPTION_EXTRA to article.description,
                CONTENT_EXTRA to article.content,
            )
    }
}
