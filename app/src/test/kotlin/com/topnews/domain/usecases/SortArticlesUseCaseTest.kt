package com.topnews.domain.usecases

import com.topnews.domain.Article
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class SortArticlesUseCaseTest {

    private val useCase = SortArticlesByRecentDateUseCase()

    @Test
    fun run_whenArticlesWithDifferentPublishedDates_thenReturnSortedByMostRecentOne() {

        //Given
        val olderArticle = mock<Article>()
        whenever(olderArticle.publishedAt).thenReturn("2022-12-10T14:52:22.1396285Z")

        val recentArticle = mock<Article>()
        whenever(recentArticle.publishedAt).thenReturn("2022-12-10T15:23:31.4045427Z")

        val articleList = listOf(olderArticle, recentArticle)

        //When
        val sortedArticleList = useCase.run(articleList)

        //Then
        sortedArticleList.shouldNotBeEmpty()
        sortedArticleList.shouldHaveSize(2)

        sortedArticleList.first().shouldBeEqualTo(recentArticle)
        sortedArticleList.last().shouldBeEqualTo(olderArticle)
    }

    @Test
    fun run_whenArticlesSamePublishedDates_thenReturnSameOrder() {

        //Given
        val articleOne = mock<Article>()
        whenever(articleOne.publishedAt).thenReturn("2022-12-10T15:23:31.4045427Z")

        val articleTwo = mock<Article>()
        whenever(articleTwo.publishedAt).thenReturn("2022-12-10T15:23:31.4045427Z")

        val articleList = listOf(articleOne, articleTwo)

        //When
        val sortedArticleList = useCase.run(articleList)

        //Then
        sortedArticleList.shouldNotBeEmpty()
        sortedArticleList.shouldHaveSize(2)

        sortedArticleList.first().shouldBeEqualTo(articleOne)
        sortedArticleList.last().shouldBeEqualTo(articleTwo)
    }
}
