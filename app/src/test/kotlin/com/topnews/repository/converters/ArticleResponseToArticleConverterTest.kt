package com.topnews.repository.converters

import com.topnews.repository.api.responses.ArticlesResponse
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class ArticleResponseToArticleConverterTest {

    @Test
    fun from_thenReturnArticle() {

        //Given
        val articlesResponse = ArticlesResponse(
            "author",
            "title",
            "description",
            "url",
            "urlToImage",
            "publishedAt",
            "content",
        )

        val converter = ArticleResponseToArticleConverter()

        //When
        val article = converter.from(articlesResponse)

        //Then
        article.author.shouldBeEqualTo("author")
        article.title.shouldBeEqualTo("title")
        article.description.shouldBeEqualTo("description")
        article.url.shouldBeEqualTo("url")
        article.urlToImage.shouldBeEqualTo("urlToImage")
        article.publishedAt.shouldBeEqualTo("publishedAt")
        article.content.shouldBeEqualTo("content")
    }
}
