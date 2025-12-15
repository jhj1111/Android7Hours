package com.sesac.domain.usecase.post

import com.sesac.domain.repository.PostRepository
import javax.inject.Inject

class GetPostListUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(query: String? = null) = repository.getPostList(query)
}