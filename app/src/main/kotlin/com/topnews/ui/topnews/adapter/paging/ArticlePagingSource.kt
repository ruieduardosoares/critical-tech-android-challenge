package com.topnews.ui.topnews.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.topnews.domain.Article
import com.topnews.domain.usecases.GetArticlesPagedAndSortedUseCase
import javax.inject.Inject

class ArticlePagingSource @Inject constructor(
    private val getArticlesPagedAndSortedUseCase: GetArticlesPagedAndSortedUseCase,
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> =
        try {
            val pageNumber = params.key ?: 1
            val pageSize = params.loadSize
            val sortedArticleList = getArticlesPagedAndSortedUseCase.execute(GetArticlesPagedAndSortedUseCase.Input(pageNumber, pageSize))
            val wasLastPage = sortedArticleList.isEmpty()
            val nextPageNumber = if (wasLastPage) null else pageNumber + 1
            LoadResult.Page(data = sortedArticleList, prevKey = null, nextKey = nextPageNumber)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
