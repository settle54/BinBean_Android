package com.binbean.admin.datasource

import android.content.Context
import android.net.Uri
import android.util.Log
import com.binbean.admin.BuildConfig
import com.binbean.admin.api.CafeRegisterService
import com.binbean.admin.dto.CafeRegisterRequest
import com.binbean.admin.dto.FloorWrapper
import com.binbean.domain.cafe.CafeDetail
import com.binbean.util.toJsonRequestBody
import com.binbean.util.uriListToMultipartParts
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class CafeRegisterRemoteDataSource @Inject constructor(private val registerApi: CafeRegisterService) {
    private val token = "Bearer ${BuildConfig.ADMIN_API_TOKEN}"

    suspend fun registerCafe(
        context: Context,
        request: CafeRegisterRequest,
        imageUris: List<Uri>,
        floorList: List<FloorWrapper>
    ): Result<Int> = try {
        val jsonCafe = request.toJsonRequestBody()
        val jsonFloor = floorList.toJsonRequestBody()
        val imageParts = uriListToMultipartParts(context, imageUris, "cafeImg")

        val response = registerApi.registerCafe(
            token = token,
            cafe = jsonCafe,
            floorPlan = jsonFloor,
            cafeImg = imageParts
        )

        handleRegisterCafeResponse(response)

    } catch (e: Exception) {
        Log.e(TAG, "예외 발생: ${e.message}", e)
        Result.failure(e)
    }


    private fun handleRegisterCafeResponse(response: Response<ResponseBody>): Result<Int> {
        return if (response.isSuccessful) {
            val bodyStr = response.body()?.string().orEmpty()
            val cafeId = JSONObject(bodyStr).optInt("cafeId", -1)
            Log.d(TAG, "등록 성공: HTTP ${response.code()} - 응답: $bodyStr")

            if (cafeId != -1) Result.success(cafeId)
            else Result.failure(Exception("응답에 카페 ID가 없습니다"))
        } else {
            val error = response.errorBody()?.string().orEmpty()
            Log.e(TAG, "등록 실패: HTTP ${response.code()} - $error")
            Result.failure(Exception("HTTP ${response.code()} - $error"))
        }
    }

    suspend fun getCafeDetail(cafeId: Int): CafeDetail {
        val response = registerApi.getCafeDetail(
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

    companion object {
        private const val TAG = "CafeRegisterDataSource"
    }
}