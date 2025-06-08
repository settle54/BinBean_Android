package com.binbean.admin.api

import com.binbean.domain.cafe.CafeDetail
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface CafeRegisterService {

    @GET("/api/cafes/{cafeId}")
    suspend fun getCafeDetail(
        @Header("Authorization") token: String,
        @Path("cafeId") cafeId: Int
    ): Response<CafeDetail>

    @Multipart
    @PUT("/api/cafes/registration")
    suspend fun modifyCafe(
        @Header("Authorization") token: String,
        @Part("cafe") cafe: RequestBody,
        @Part cafeImg: List<MultipartBody.Part>? = null
    ): Response<ResponseBody>

    @Multipart
    @POST("/api/cafes/registration")
    suspend fun registerCafe(
        @Header("Authorization") token: String,
        @Part("cafe") cafe: RequestBody,
        @Part("floorPlan") floorPlan: RequestBody,
        @Part cafeImg: List<MultipartBody.Part>? = null
    ): Response<ResponseBody>
}