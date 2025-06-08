package com.binbean.data.cafe.remote

import com.binbean.domain.FavoriteCafeResponse
import com.binbean.domain.cafe.CafeDetail
import com.binbean.domain.cafe.FloorPlanResponse
import com.binbean.domain.cafe.ReviewPostRequest
import com.binbean.domain.cafe.ServerCafe
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CafeRetrofitServerService {
    @GET("/api/markers")
    suspend fun searchCafesInBounds(
        @Header("Authorization") token: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<List<ServerCafe>>

    @GET("/api/cafes/{cafeId}")
    suspend fun getCafeDetail(
        @Header("Authorization") token: String,
        @Path("cafeId") cafeId: Int
    ): Response<CafeDetail>

    @GET("/api/users/favorites")
    suspend fun getFavoriteCafes(
        @Header("Authorization") token: String
    ): Response<List<FavoriteCafeResponse>>

    @POST("/api/reviews/{cafeId}")
    suspend fun postReview(
        @Header("Authorization") token: String,
        @Path("cafeId") cafeId: Int,
        @Body reviewRequest: ReviewPostRequest
    ): Response<Unit>

    @GET("/api/cafes/floor-plan/{floorPlanId}")
    suspend fun getFloorPlan(
        @Header("Authorization") token: String,
        @Path("floorPlanId") floorPlanId: Int
    ): Response<List<FloorPlanResponse>>
}