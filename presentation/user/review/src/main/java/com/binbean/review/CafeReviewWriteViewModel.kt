package com.binbean.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binbean.domain.cafe.ReviewPostRequest
import com.binbean.domain.cafe.usecase.PostCafeReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeReviewWriteViewModel @Inject constructor(
    private val postCafeReviewUseCase: PostCafeReviewUseCase
) : ViewModel() {

    private val _reviewPostState = MutableLiveData<Result<Unit>>()
    val reviewPostState: LiveData<Result<Unit>> = _reviewPostState

    fun postReview(cafeId: Int, review: ReviewPostRequest) {
        viewModelScope.launch {
            val result = postCafeReviewUseCase(cafeId, review)
            _reviewPostState.value = result
        }
    }
}