package com.topnews.domain.usecases

import com.topnews.domain.Article
import com.topnews.repository.ArticlesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.internal.assertFails
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class GetArticlesPagedAndSortedUseCaseTest {

    @Mock
    lateinit var newsRepository: ArticlesRepository

    @Mock
    lateinit var sortArticlesUseCase: SortArticlesByRecentDateUseCase

    @Test
    fun execute_whenReturnedTopHeadlines_thenReturnThemSorted() = runTest {

        //Given
        val dispatcher = StandardTestDispatcher(this.testScheduler)
        val useCase = GetArticlesPagedAndSortedUseCase(dispatcher, newsRepository, sortArticlesUseCase)

        val repoArticleList = listOf<Article>(mock())
        whenever(newsRepository.getTopHeadlines(any(), any())).thenReturn(repoArticleList)

        val sortedArticleList = listOf<Article>(mock())
        whenever(sortArticlesUseCase.run(any())).thenReturn(sortedArticleList)

        val pageNumber = 2
        val pageSize = 13
        val input = GetArticlesPagedAndSortedUseCase.Input(pageNumber, pageSize)

        //When
        val articleList = useCase.execute(input)

        //Then
        articleList.shouldBeEqualTo(sortedArticleList)

        verify(newsRepository, only()).getTopHeadlines(eq(2), eq(13))

        verify(sortArticlesUseCase, only()).run(eq(repoArticleList))
    }

    @Test
    fun execute_whenRepositoryThrowsException_thenCatchItAndDoNothingMore() = runTest {

        //Given
        val dispatcher = StandardTestDispatcher(this.testScheduler)
        val useCase = GetArticlesPagedAndSortedUseCase(dispatcher, newsRepository, sortArticlesUseCase)

        val repositoryException = RuntimeException("some repository error")
        whenever(newsRepository.getTopHeadlines(any(), any())).thenThrow(repositoryException)

        val pageNumber = 2
        val pageSize = 13
        val input = GetArticlesPagedAndSortedUseCase.Input(pageNumber, pageSize)

        //When
        val exception = assertFails { useCase.execute(input) }

        //Then
        exception.shouldBeInstanceOf<RuntimeException>()
        exception.message.shouldBeEqualTo("some repository error")

        verify(newsRepository, only()).getTopHeadlines(eq(2), eq(13))

        verifyNoInteractions(sortArticlesUseCase)
    }

    @Test
    fun execute_whenSortUseCaseThrowsException_thenCatchItAndDoNothingMore() = runTest {

        //Given
        val dispatcher = StandardTestDispatcher(this.testScheduler)
        val useCase = GetArticlesPagedAndSortedUseCase(dispatcher, newsRepository, sortArticlesUseCase)

        val repoArticleList = listOf<Article>(mock())
        whenever(newsRepository.getTopHeadlines(any(), any())).thenReturn(repoArticleList)

        val sortedUseCaseException = RuntimeException("some repository error")
        whenever(sortArticlesUseCase.run(any())).thenThrow(sortedUseCaseException)

        val pageNumber = 2
        val pageSize = 13
        val input = GetArticlesPagedAndSortedUseCase.Input(pageNumber, pageSize)

        //When
        val exception = assertFails { useCase.execute(input) }

        //Then
        exception.shouldBeInstanceOf<RuntimeException>()
        exception.message.shouldBeEqualTo("some repository error")

        verify(newsRepository, only()).getTopHeadlines(eq(2), eq(13))

        verify(sortArticlesUseCase, only()).run(eq(repoArticleList))
    }
}
