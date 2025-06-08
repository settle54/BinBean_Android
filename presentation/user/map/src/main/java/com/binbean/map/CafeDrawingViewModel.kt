package com.binbean.map

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binbean.domain.cafe.CafeDetail
import com.binbean.domain.cafe.FloorPlanResponse
import com.binbean.domain.cafe.usecase.GetCafeDetailUseCase
import com.binbean.domain.cafe.usecase.GetFloorPlanUseCase
import com.binbean.user.dto.DetectRequest
import com.binbean.user.dto.DetectResponse
import com.binbean.user.dto.Position
import com.binbean.user.repositoryImpl.DetectRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeDrawingViewModel @Inject constructor(
    private val detectRepository: DetectRepositoryImpl,
    private val getCafeDetailUseCase: GetCafeDetailUseCase,
    private val getFloorPlanUseCase: GetFloorPlanUseCase
) : ViewModel() {

    private val _cafeDetail = MutableLiveData<CafeDetail>()
    val cafeDetail: LiveData<CafeDetail> = _cafeDetail

    private val _floorPlans = MutableLiveData<List<FloorPlanResponse>>()
    val floorPlans: LiveData<List<FloorPlanResponse>> = _floorPlans

    private val _detectResult = MutableLiveData<DetectResponse>()
    val detectResult: LiveData<DetectResponse> = _detectResult

    // 더미 리퀘스트

    // 더미 플로어 넘버
    private val floorNumber = 1

    fun detect(context: Context, request: DetectRequest, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val response = detectRepository.detect(context, floorNumber, request, imageUri)
                response?.let {
                    _detectResult.value = it
                }
            } catch (e: Exception) {
                Log.e("CafeDrawingVM", "도면 감지 실패", e)
            }
        }
    }

    fun loadCafeDetail(cafeId: Int) {
        viewModelScope.launch {
            try {
                val detail = getCafeDetailUseCase(cafeId)
                _cafeDetail.value = detail
            } catch (e: Exception) {
                Log.e("CafeDrawingVM", "카페 상세 조회 실패", e)
            }
        }
    }

    fun loadFloorPlan(floorPlanId: Int) {
        viewModelScope.launch {
            try {
                val plans = getFloorPlanUseCase(floorPlanId)
                _floorPlans.value = plans
                Log.d("CafeDrawingVM", plans.toString())
            } catch (e: Exception) {
                Log.e("CafeDrawingVM", "도면 조회 실패", e)
            }
        }
    }
}