package com.binbean.util

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun Any.toJsonRequestBody(): RequestBody {
    val gson = Gson()
    return gson.toJson(this).toRequestBody("application/json".toMediaTypeOrNull())
}