package com.binbean.register

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binbean.admin.repositoryImpl.CafeRegisterRepositoryImpl
import com.binbean.domain.cafe.CafeDetail
import com.binbean.domain.cafe.CafeInfoImgItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeModifyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cafeRepository: CafeRegisterRepositoryImpl
): ViewModel() {
    private val _cafeDetail = MutableLiveData<CafeDetail>()
    val cafeDetail: LiveData<CafeDetail> = _cafeDetail

    private val _photoList = MutableLiveData<List<CafeInfoImgItem>>()
    val photoList: LiveData<List<CafeInfoImgItem>> = _photoList

    init {
        loadCafeId()
    }

    /**
     * cafeId 반환 함수
     */
    fun getCafeId(): Int {
        return _cafeDetail.value?.id ?: -1
    }

    /**
     * 카페 상세정보 반환 함수
     */
    fun getCafeDetail(): CafeDetail? {
        return _cafeDetail.value
    }

    /**
     * prefs에서 cafeId 읽어오는 함수
     */
    private fun loadCafeId() {
        val id = context.getSharedPreferences("binbean_prefs", Context.MODE_PRIVATE)
            .getInt("cafeId", -1)
        Log.d(TAG, "$id")
        loadCafeDetail(id)
    }

    /**
     * cafeId로 서버에서 카페 상세정보 받아오는 함수
     */
    private fun loadCafeDetail(cafeId: Int) {
        viewModelScope.launch {
            try {
                val result = cafeRepository.getCafeDetail(cafeId)
                _cafeDetail.value = result
            } catch (e: Exception) {
                Log.e("CafeBottomSheetVM", "카페 상세 불러오기 실패", e)
            }
        }
    }

    companion object{
        private const val TAG = "CafeModifyViewModel"
    }
}