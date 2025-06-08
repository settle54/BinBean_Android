package com.binbean.data.cafe.repositoryImpl

import android.util.Log
import com.binbean.data.BuildConfig
import com.binbean.data.cafe.remote.CafeRetrofitServerService
import com.binbean.data.cafe.remote.CafeRetrofitService
import com.binbean.data.cafe.toCafe
import com.binbean.domain.FavoriteCafeResponse
import com.binbean.domain.cafe.Cafe
import com.binbean.domain.cafe.CafeDetail
import com.binbean.domain.cafe.FloorPlanResponse
import com.binbean.domain.cafe.ReviewPostRequest
import com.binbean.domain.cafe.ServerCafe
import com.binbean.domain.cafe.repository.CafeRepository
import javax.inject.Inject

class CafeRepositoryImpl @Inject constructor(
    private val cafeRetrofitService: CafeRetrofitService,
    private val cafeRetrofitServerService: CafeRetrofitServerService
): CafeRepository {
    private val token = "Bearer ${BuildConfig.USER_API_TOKEN}"

    override suspend fun searchCafesInBounds(
        latitude: Double,
        longitude: Double
    ): List<Cafe> {
        val delta = 0.01 // 예시: 1km 정도 반경
        val rect = "${longitude - delta},${latitude - delta},${longitude + delta},${latitude + delta}"

        val response = cafeRetrofitService.searchKeyword("카페", rect)
        if (response.isSuccessful) {
            return response.body()?.documents?.map {
                it.toCafe()
            } ?: emptyList()
        } else {
            throw Exception("API 오류")
        }
    }

    override suspend fun searchServerCafesInBounds(
        latitude: Double,
        longitude: Double
    ): List<ServerCafe> {
        val response = cafeRetrofitServerService.searchCafesInBounds(
            token = token,
            latitude = latitude,
            longitude = longitude
        )

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e("ServerCafe", "서버 오류 내용: $errorBody")
            throw Exception("서버 API 오류: ${response.code()} - $errorBody")
        }
    }

    override suspend fun searchCafesByKeyword(keyword: String): List<Cafe> {
        val response = cafeRetrofitService.searchKeyword(keyword, "")
        if (response.isSuccessful) {
            return response.body()?.documents?.map {
                it.toCafe()
            } ?: emptyList()
        } else {
            throw Exception("API 오류")
        }
    }

    override suspend fun getCafeDetail(cafeId: Int): CafeDetail {
        val response = cafeRetrofitServerService.getCafeDetail(
            token = token,
            cafeId = cafeId
        )
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("응답 본문 없음")
        } else {
            val error = response.errorBody()?.string()
            throw Exception("상세 조회 실패: ${response.code()} - $error")
        }
    }

    override suspend fun getFavoriteCafes(): List<FavoriteCafeResponse> {
        val response = cafeRetrofitServerService.getFavoriteCafes(
            token
        )
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            val error = response.errorBody()?.string()
            throw Exception("즐겨찾기 조회 실패: ${response.code()} - $error")
        }
    }

    override suspend fun postReview(cafeId: Int, review: ReviewPostRequest): Result<Unit> {
        return try {
            val response = cafeRetrofitServerService.postReview(
                token = token,
                cafeId = cafeId,
                reviewRequest = review
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string()
                Result.failure(Exception("리뷰 등록 실패: ${response.code()} - $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFloorPlan(floorPlanId: Int): List<FloorPlanResponse> {
        val response = cafeRetrofitServerService.getFloorPlan(
            token = token,
            floorPlanId = floorPlanId
        )

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            val error = response.errorBody()?.string()
            Log.e("CafeRepositoryImpl", "도면 조회 실패: $error")
            throw Exception("도면 조회 실패: ${response.code()} - $error")
        }
    }
}