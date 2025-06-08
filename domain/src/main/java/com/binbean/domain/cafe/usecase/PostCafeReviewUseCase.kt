package com.binbean.domain.cafe.usecase

import com.binbean.domain.cafe.ReviewPostRequest
import com.binbean.domain.cafe.repository.CafeRepository
import javax.inject.Inject

class PostCafeReviewUseCase @Inject constructor(
    private val cafeRepository: CafeRepository
) {
    suspend operator fun invoke(cafeId: Int, review: ReviewPostRequest): Result<Unit> {
        return cafeRepository.postReview(cafeId, review)
    }
}