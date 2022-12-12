package com.topnews.domain.usecases

import com.topnews.di.IODispatcher
import com.topnews.domain.Article
import com.topnews.repository.ArticlesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetArticlesPagedAndSortedUseCase @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val newsRepository: ArticlesRepository,
    private val sortArticlesUseCase: SortArticlesByRecentDateUseCase,
) {

    data class Input(val pageNumber: Int, val pageSize: Int)

    suspend fun execute(input: Input): List<Article> = withContext(dispatcher) {
        val articleList = newsRepository.getTopHeadlines(input.pageNumber,  input.pageSize)
        sortArticlesUseCase.run(articleList)
    }
}
