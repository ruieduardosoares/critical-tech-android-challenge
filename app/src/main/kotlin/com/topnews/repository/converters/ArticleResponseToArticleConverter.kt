package com.topnews.repository.converters

import com.topnews.domain.Article
import com.topnews.repository.api.responses.ArticlesResponse
import javax.inject.Inject

class ArticleResponseToArticleConverter @Inject constructor() {

    fun from(articlesResponse: ArticlesResponse): Article =
        Article(
            articlesResponse.author,
            articlesResponse.title,
            articlesResponse.description,
            articlesResponse.url,
            articlesResponse.urlToImage,
            articlesResponse.publishedAt,
            articlesResponse.content
        )
}
