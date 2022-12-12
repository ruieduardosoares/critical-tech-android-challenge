package com.topnews.repository

import com.topnews.NewsSourceConfig
import com.topnews.repository.api.NewsApi
import com.topnews.repository.api.responses.ArticlesResponse
import com.topnews.repository.api.responses.TopHeadlinesResponse
import com.topnews.repository.converters.ArticleResponseToArticleConverter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.internal.assertFails
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class ArticlesRepositoryTest {

    @Mock
    lateinit var newsSourceConfig: NewsSourceConfig

    @Mock
    lateinit var newsApi: NewsApi

    @Mock
    lateinit var articleResponseToArticleConverter: ArticleResponseToArticleConverter

    @InjectMocks
    lateinit var repository: ArticlesRepository

    @Test
    fun getTopHeadlines_whenResponseNotSuccessful_thenThrowRuntimeException() = runTest {

        //Given
        val response = mock<Response<TopHeadlinesResponse>>()
        whenever(response.isSuccessful).thenReturn(false)
        whenever(response.code()).thenReturn(404)

        whenever(newsSourceConfig.name).thenReturn("some-source")

        whenever(newsApi.getTopHeadlines(any(), any(), any())).thenReturn(response)

        val pageNumber = 3
        val pageSize = 12

        //When
        val exception = assertFails { repository.getTopHeadlines(pageNumber, pageSize) }

        //Then
        exception.shouldBeInstanceOf<RuntimeException>()
        exception.message.shouldBeEqualTo("Http error code 404")

        verify(newsSourceConfig, only()).name

        verify(newsApi, only()).getTopHeadlines(eq("some-source"), eq(3), eq(12))

        verify(response).isSuccessful
        verify(response).code()
        verifyNoMoreInteractions(response)

        verifyNoInteractions(articleResponseToArticleConverter)
    }

    @Test
    fun getTopHeadlines_whenResponseSuccessfulButNullBody_thenReturnEmptyList() = runTest {

        //Given
        val response = mock<Response<TopHeadlinesResponse>>()
        whenever(response.isSuccessful).thenReturn(true)
        whenever(response.body()).thenReturn(null)

        whenever(newsSourceConfig.name).thenReturn("some-source")

        whenever(newsApi.getTopHeadlines(any(), any(), any())).thenReturn(response)

        val pageNumber = 4
        val pageSize = 13

        //When
        val articleList = repository.getTopHeadlines(pageNumber, pageSize)

        //Then
        articleList.shouldBeEmpty()

        verify(newsSourceConfig, only()).name

        verify(newsApi, only()).getTopHeadlines(eq("some-source"), eq(4), eq(13))

        verify(response).isSuccessful
        verify(response).body()
        verifyNoMoreInteractions(response)

        verifyNoInteractions(articleResponseToArticleConverter)
    }

    @Test
    fun getTopHeadlines_whenResponseSuccessfulBodyNotNull_thenReturnArticlesList() = runTest {

        //Given
        val articlesResponseOne = mock<ArticlesResponse>()
        val articlesResponseTwo = mock<ArticlesResponse>()
        val expectedArticleResponseList = listOf(articlesResponseOne, articlesResponseTwo)
        val topHeadlinesResponse = TopHeadlinesResponse("ok", 2, expectedArticleResponseList)

        val response = mock<Response<TopHeadlinesResponse>>()
        whenever(response.isSuccessful).thenReturn(true)
        whenever(response.body()).thenReturn(topHeadlinesResponse)

        whenever(newsSourceConfig.name).thenReturn("some-source")

        whenever(newsApi.getTopHeadlines(any(), any(), any())).thenReturn(response)

        whenever(articleResponseToArticleConverter.from(any())).thenReturn(mock())

        val pageNumber = 5
        val pageSize = 11

        //When
        val articleList = repository.getTopHeadlines(pageNumber, pageSize)

        //Then
        articleList.shouldNotBeEmpty()
        articleList.shouldHaveSize(2)

        verify(newsSourceConfig, only()).name

        verify(newsApi, only()).getTopHeadlines(eq("some-source"), eq(5), eq(11))

        verify(response).isSuccessful
        verify(response).body()
        verifyNoMoreInteractions(response)

        verify(articleResponseToArticleConverter).from(eq(articlesResponseOne))
        verify(articleResponseToArticleConverter).from(eq(articlesResponseTwo))
        verifyNoMoreInteractions(articleResponseToArticleConverter)
    }
}
