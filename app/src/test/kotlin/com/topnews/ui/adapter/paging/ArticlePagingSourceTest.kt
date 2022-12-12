package com.topnews.ui.adapter.paging

import androidx.paging.PagingSource
import com.topnews.domain.Article
import com.topnews.domain.usecases.GetArticlesPagedAndSortedUseCase
import com.topnews.ui.topnews.adapter.paging.ArticlePagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class ArticlePagingSourceTest {

    @Mock
    lateinit var getArticlesPagedAndSortedUseCase: GetArticlesPagedAndSortedUseCase

    @InjectMocks
    lateinit var pagingSource: ArticlePagingSource

    @Test
    fun load_whenInitialLoadAndArticlesAreAvailable_thenReturnPageWithArticlesListAndNextKeyAsNextPageNumber() = runTest {

        //Given
        val sortedArticleList = listOf<Article>(mock())
        whenever(getArticlesPagedAndSortedUseCase.execute(any())).thenReturn(sortedArticleList)

        val refreshParams = PagingSource.LoadParams.Refresh<Int>(key = null, loadSize = 3, placeholdersEnabled = false)

        //When
        val loadResult = pagingSource.load(refreshParams)

        //Then
        loadResult.shouldBeInstanceOf<PagingSource.LoadResult.Page<Int, Article>>()
        loadResult as PagingSource.LoadResult.Page<Int, Article>

        loadResult.data.shouldBeEqualTo(sortedArticleList)
        loadResult.prevKey.shouldBeNull()
        loadResult.nextKey.shouldBeEqualTo(2)

        val expectedInput = GetArticlesPagedAndSortedUseCase.Input(1, 3)
        verify(getArticlesPagedAndSortedUseCase, only()).execute(eq(expectedInput))
    }

    @Test
    fun load_whenMiddlePageLoadAndArticlesAreAvailable_thenReturnPageWithArticlesListAndNextKeyAsNextPageNumber() = runTest {

        //Given
        val sortedArticleList = listOf<Article>(mock())
        whenever(getArticlesPagedAndSortedUseCase.execute(any())).thenReturn(sortedArticleList)

        val refreshParams = PagingSource.LoadParams.Refresh(key = 2, loadSize = 3, placeholdersEnabled = false)

        //When
        val loadResult = pagingSource.load(refreshParams)

        //Then
        loadResult.shouldBeInstanceOf<PagingSource.LoadResult.Page<Int, Article>>()
        loadResult as PagingSource.LoadResult.Page<Int, Article>

        loadResult.data.shouldBeEqualTo(sortedArticleList)
        loadResult.prevKey.shouldBeNull()
        loadResult.nextKey.shouldBeEqualTo(3)

        val expectedInput = GetArticlesPagedAndSortedUseCase.Input(2, 3)
        verify(getArticlesPagedAndSortedUseCase, only()).execute(eq(expectedInput))
    }

    @Test
    fun load_whenLastPageLoadBecauseArticlesNotAvailable_thenReturnPageWithoutArticlesListAndNextKeyNull() = runTest {

        //Given
        val sortedArticleList = emptyList<Article>()
        whenever(getArticlesPagedAndSortedUseCase.execute(any())).thenReturn(sortedArticleList)

        val refreshParams = PagingSource.LoadParams.Refresh(key = 2, loadSize = 5, placeholdersEnabled = false)

        //When
        val loadResult = pagingSource.load(refreshParams)

        //Then
        loadResult.shouldBeInstanceOf<PagingSource.LoadResult.Page<Int, Article>>()
        loadResult as PagingSource.LoadResult.Page<Int, Article>

        loadResult.data.shouldBeEqualTo(sortedArticleList)
        loadResult.prevKey.shouldBeNull()
        loadResult.nextKey.shouldBeNull()

        val expectedInput = GetArticlesPagedAndSortedUseCase.Input(2, 5)
        verify(getArticlesPagedAndSortedUseCase, only()).execute(eq(expectedInput))
    }

}
