package com.topnews.repository.api

import com.topnews.repository.api.responses.TopHeadlinesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    @Headers("X-Api-Key: 8a4edca62c6e4bd38a88419ac5c7edd9")
    suspend fun getTopHeadlines(
        @Query("sources") sources: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
    ): Response<TopHeadlinesResponse>
}
