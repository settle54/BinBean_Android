package com.binbean.user.repositoryImpl

import android.content.Context
import android.net.Uri
import android.util.Log
import com.binbean.user.BuildConfig
import com.binbean.user.api.DetectService
import com.binbean.user.dto.DetectRequest
import com.binbean.util.uriToMultipartPart
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class DetectRepositoryImpl @Inject constructor(
    private val detectService: DetectService
) {
    private val token = "Bearer ${BuildConfig.USER_API_TOKEN}"

    suspend fun detect(context: Context, floorNumber: Int, request: DetectRequest, imageUri: Uri) {
        val gson = Gson()
        val jsonFloor = gson.toJson(request)
            .toRequestBody("application/json".toMediaTypeOrNull())
        val jsonFNumber = floorNumber.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
        val imagePart = uriToMultipartPart(context, imageUri, "image", 0)

        Log.d(TAG, imagePart.toString())
        try {
            val response = detectService.detect(
                token = token,
                floorNumber = jsonFNumber,
                floorList = jsonFloor,
                image = imagePart
            )
            if (response.isSuccessful) {
                Log.d(
                    TAG,
                    "통신 성공: HTTP ${response.code()} - ${response.body()?.toString() ?: "No body"}"
                )
            } else {
                Log.e(TAG, "통신 실패: HTTP ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "예외 발생: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "DetectRepositoryImpl"
    }
}