package com.binbean.admin.repositoryImpl

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import com.binbean.admin.datasource.CafeRegisterRemoteDataSource
import com.binbean.admin.dto.CafeRegisterRequest
import com.binbean.admin.dto.FloorWrapper
import com.binbean.domain.cafe.CafeDetail
import javax.inject.Inject

class CafeRegisterRepositoryImpl @Inject constructor(
    private val cafeRegisterRemoteDataSource: CafeRegisterRemoteDataSource
) {

    // 더미 도면
//    val floorList: List<FloorWrapper> = listOf(
//        FloorWrapper(
//            floorList = FloorDetail(
//                borderPosition = listOf(Position(0, 0), Position(10, 0)),
//                seatPosition = listOf(Position(2, 3), Position(3, 3)),
//                doorPosition = listOf(Position(0, 5)),
//                counterPosition = listOf(Position(5, 1)),
//                toiletPosition = listOf(Position(8, 6)),
//                windowPosition = listOf(Position(1, 0))
//            ),
//            floorNumber = 1,
//            maxSeats = 20
//        )
//    )

    /**
     * 카페를 등록하는 함수
     */
    suspend fun registerCafe(
        context: Context,
        request: CafeRegisterRequest,
        imageUris: List<Uri>,
        floorList: List<FloorWrapper>
    ): Boolean {
        val result =
            cafeRegisterRemoteDataSource.registerCafe(context, request, imageUris, floorList)

        return result.fold(
            onSuccess = { cafeId ->
                context.getSharedPreferences("binbean_prefs", Context.MODE_PRIVATE)
                    .edit {
                        putInt("cafeId", cafeId)
                    }
                Log.d(TAG, "pref 등록 성공: $cafeId")
                true
            },
            onFailure = {
                Log.w(TAG, "pref 등록 실패. ${it.message}")
                false
            }
        )
    }

    suspend fun getCafeDetail(cafeId: Int): CafeDetail {
        return cafeRegisterRemoteDataSource.getCafeDetail(cafeId)
    }

    companion object {
        private const val TAG = "CafeRegisterRepositoryImpl"
    }
}