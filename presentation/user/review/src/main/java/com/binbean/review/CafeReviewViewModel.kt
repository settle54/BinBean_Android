package com.binbean.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binbean.domain.cafe.CafeDetail
import com.binbean.domain.cafe.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CafeReviewViewModel @Inject constructor(): ViewModel() {
    private val _reviewList = MutableLiveData<List<Review>>()
    val reviewList: LiveData<List<Review>> = _reviewList

    fun loadReviewsFromDetail(detail: CafeDetail) {
        _reviewList.value = detail.reviewResponse
    }

    fun loadDummyReviews() {
        _reviewList.value = listOf(
            Review(1, "2025.02.21", "홍길동", 4.0, "Lorem Ipsum is simply dummy text...", true, emptyList()),
            Review(2, "2025.02.21", "심청이", 3.0, "Lorem Ipsum is simply dummy text...", false, emptyList())
        )
    }
}