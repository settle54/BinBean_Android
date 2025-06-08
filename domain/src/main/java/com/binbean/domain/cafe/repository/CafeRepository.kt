package com.binbean.domain.cafe.repository

import com.binbean.domain.FavoriteCafeResponse
import com.binbean.domain.cafe.Cafe
import com.binbean.domain.cafe.CafeDetail
import com.binbean.domain.cafe.FloorPlanResponse
import com.binbean.domain.cafe.ReviewPostRequest
import com.binbean.domain.cafe.ServerCafe

interface CafeRepository {
    suspend fun searchCafesInBounds(
        latitude: Double,
        longitude: Double
    ): List<Cafe>

    suspend fun searchServerCafesInBounds(
        latitude: Double,
        longitude: Double
    ): List<ServerCafe>

    suspend fun searchCafesByKeyword(
        keyword: String
    ): List<Cafe>

    suspend fun getCafeDetail(
        cafeId: Int
    ): CafeDetail


    suspend fun getFavoriteCafes(

    ): List<FavoriteCafeResponse>

    suspend fun postReview(
        cafeId: Int,
        review: ReviewPostRequest
    ): Result<Unit>

    suspend fun getFloorPlan(
        floorPlanId: Int
    ): List<FloorPlanResponse>
}