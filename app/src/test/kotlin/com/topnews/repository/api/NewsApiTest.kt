package com.topnews.repository.api

import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import okio.IOException
import org.amshove.kluent.fail
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSingleItem
import org.amshove.kluent.shouldNotBeNull
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.EOFException

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class NewsApiTest {

    @get:Rule
    val server = MockWebServer()

    @Test
    fun getTopHeadlines_whenBodyNotNullAndIsExpectedBody_thenReturnResponseObjectRepresentation() = runTest {

        //Given
        val rawResponse = this.javaClass.getResource("/news_api_response.json")?.readText()
        rawResponse.shouldNotBeNull()

        val mockResponse = MockResponse()
        mockResponse.setBody(rawResponse)

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val newsApi = retrofit.create(NewsApi::class.java)

        server.enqueue(mockResponse)

        //When
        val topHeadlineResponse = newsApi.getTopHeadlines("bbc-news")

        //Then
        topHeadlineResponse.isSuccessful.shouldBeTrue()
        topHeadlineResponse.code().shouldBeEqualTo(200)

        val body = topHeadlineResponse.body()
        body.shouldNotBeNull()

        body.status.shouldBeEqualTo("ok")
        body.totalResults.shouldBeEqualTo(37)

        val articles = body.articles
        articles.shouldHaveSingleItem()

        val articlesResponse = articles.first()
        articlesResponse.author.shouldBeEqualTo("Rob Picheta")
        articlesResponse.title.shouldBeEqualTo("Harry compares Meghan to Diana and criticizes royals' 'unconscious bias' in Netflix documentary - CNN")
        articlesResponse.description.shouldBeEqualTo("Prince Harry and Meghan, Duchess of Sussex, have taken aim at \"unconscious bias\" inside the royal family and defended their decision to quit the institution, as their highly anticipated Netflix documentary series threatens to deepen the split between the coup…")
        articlesResponse.url.shouldBeEqualTo("https://www.cnn.com/2022/12/08/entertainment/harry-meghan-netflix-documentary-release-intl/index.html")
        articlesResponse.urlToImage.shouldBeEqualTo("https://media.cnn.com/api/v1/images/stellar/prod/221205082436-01-netflix-harry-meghan-trailer-screenshot.jpg?c=16x9&q=w_800,c_fill")
        articlesResponse.publishedAt.shouldBeEqualTo("2022-12-08T11:30:00Z")
        articlesResponse.content.shouldBeEqualTo("Prince Harry and Meghan, Duchess of Sussex, have taken aim at unconscious bias inside the royal family and defended their decision to quit the institution, as their highly anticipated Netflix documen… [+7082 chars]")
    }

    @Test
    fun getTopHeadlines_whenBodyNotNullAndNotExpectedBodyStructure_thenReturnThrowJsonDataException() = runTest {

        //Given
        val badRawResponse = "{ \"someField\": 123 }"
        badRawResponse.shouldNotBeNull()

        val mockResponse = MockResponse()
        mockResponse.setBody(badRawResponse)

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val newsApi = retrofit.create(NewsApi::class.java)

        server.enqueue(mockResponse)

        //When
        try {
            newsApi.getTopHeadlines("bbc-news")
            fail("should fail parsing response")
        } catch (exception: RuntimeException) {
            exception.shouldBeInstanceOf<JsonDataException>()
        }
    }

    @Test
    fun getTopHeadlines_whenBodyNotNullAndEmptyJsonBody_thenReturnThrowJsonDataException() = runTest {

        //Given
        val badRawResponse = "{}"
        badRawResponse.shouldNotBeNull()

        val mockResponse = MockResponse()
        mockResponse.setBody(badRawResponse)

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val newsApi = retrofit.create(NewsApi::class.java)

        server.enqueue(mockResponse)

        //When
        try {
            newsApi.getTopHeadlines("bbc-news")
            fail("should fail parsing response")
        } catch (exception: RuntimeException) {
            exception.shouldBeInstanceOf<JsonDataException>()
        }
    }

    @Test
    fun getTopHeadlines_whenFailToMakeConnection_thenReturnThrowJsonDataException() = runTest {

        //Given
        val mockResponse = MockResponse()
        mockResponse.socketPolicy = SocketPolicy.FAIL_HANDSHAKE

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val newsApi = retrofit.create(NewsApi::class.java)

        server.enqueue(mockResponse)

        //When
        try {
            newsApi.getTopHeadlines("bbc-news")
            fail("should handshake")
        } catch (exception: IOException) {
            exception.shouldBeInstanceOf<EOFException>()
        }
    }

    @Test
    fun getTopHeadlines_whenNotFound_thenReturnThrowJsonDataException() = runTest {

        //Given
        val mockResponse = MockResponse()
        mockResponse.setResponseCode(404)

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val newsApi = retrofit.create(NewsApi::class.java)

        server.enqueue(mockResponse)

        //When
        val topHeadlinesResponse = newsApi.getTopHeadlines("bbc-news")

        topHeadlinesResponse.isSuccessful.shouldBeFalse()
        topHeadlinesResponse.code().shouldBeEqualTo(404)
        topHeadlinesResponse.body().shouldBeNull()
    }
}
