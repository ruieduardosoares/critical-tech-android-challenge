package com.topnews.domain.usecases

import com.topnews.domain.Article
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SortArticlesByRecentDateUseCase @Inject constructor() {

    fun run(articleList: List<Article>): List<Article> = articleList.sortedWith(COMPARATOR)

    private class ArticlesComparator : Comparator<Article> {

        private val formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC.normalized())

        override fun compare(left: Article, rigth: Article): Int {
            val leftPublishedAtAsMilli = LocalDateTime.parse(left.publishedAt, formatter).toInstant(ZoneOffset.UTC).toEpochMilli()
            val rightPublishedAtAsMilli = LocalDateTime.parse(rigth.publishedAt, formatter).toInstant(ZoneOffset.UTC).toEpochMilli()
            return rightPublishedAtAsMilli.compareTo(leftPublishedAtAsMilli)
        }
    }

    private companion object {

        private val COMPARATOR = ArticlesComparator()
    }
}
