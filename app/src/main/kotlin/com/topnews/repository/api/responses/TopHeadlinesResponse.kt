package com.topnews.repository.api.responses

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopHeadlinesResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticlesResponse>,
)
