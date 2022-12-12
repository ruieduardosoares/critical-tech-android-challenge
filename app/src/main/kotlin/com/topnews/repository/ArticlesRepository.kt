package com.topnews.repository

import com.topnews.NewsSourceConfig
import com.topnews.domain.Article
import com.topnews.repository.api.NewsApi
import com.topnews.repository.converters.ArticleResponseToArticleConverter
import javax.inject.Inject

class ArticlesRepository @Inject constructor(
    private val newsSourceConfig: NewsSourceConfig,
    private val newsApi: NewsApi,
    private val articleResponseToArticleConverter: ArticleResponseToArticleConverter,
) {

    suspend fun getTopHeadlines(pageNumber: Int, pageSize: Int): List<Article> {
        val response = newsApi.getTopHeadlines(newsSourceConfig.name, pageNumber, pageSize)
        if (response.isSuccessful.not()) {
            throw RuntimeException("Http error code ${response.code()}")
        } else {
            return response.body()
                ?.articles
                ?.map(articleResponseToArticleConverter::from)
                .orEmpty()
        }
    }
}
