package com.binbean.map.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binbean.domain.cafe.Cafe
import com.binbean.domain.cafe.ServerCafe
import com.binbean.domain.cafe.usecase.SearchCafesInBoundsUseCase
import com.binbean.domain.cafe.usecase.SearchServerCafesInBoundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val searchCafesInBoundsUseCase: SearchCafesInBoundsUseCase,
    private val searchServerCafesInBoundsUseCase: SearchServerCafesInBoundsUseCase,
): ViewModel(){

    private val _cafeList = MutableLiveData<List<Cafe>>()
    val cafeList: LiveData<List<Cafe>> = _cafeList

    private val _serverCafeList = MutableLiveData<List<ServerCafe>>()
    val serverCafeList: LiveData<List<ServerCafe>> = _serverCafeList

    private val _selectedCafe = MutableLiveData<Cafe?>()
    val selectedCafe: LiveData<Cafe?> = _selectedCafe

    private val _selectedServerCafe = MutableLiveData<ServerCafe?>()
    val selectedServerCafe: LiveData<ServerCafe?> = _selectedServerCafe

    fun loadCafes(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val cafes = searchCafesInBoundsUseCase(lat, lng)
                _cafeList.value = cafes
                Log.d("MapViewModel", "불러온 카페 리스트: $cafes")
            } catch (e: Exception) {
                Log.e("MapViewModel", "카페 불러오기 실패", e)
            }
        }
    }

    fun loadServerCafes(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val serverCafes = searchServerCafesInBoundsUseCase(lat, lng)
                _serverCafeList.value = serverCafes
                Log.d("MapViewModel", "불러온 서버 카페 리스트: $serverCafes")
            } catch (e: Exception) {
                Log.e("MapViewModel", "서버 카페 불러오기 실패", e)
            }
        }
    }

    fun selectCafe(cafe: Cafe) {
        _selectedCafe.value = cafe
        Log.d("MapViewModel", "선택된 카페: $cafe")
    }

    fun selectServerCafe(cafe: ServerCafe) {
        _selectedServerCafe.value = cafe
        Log.d("MapViewModel", "선택된 카페: $cafe")
    }

    fun clearSelectedCafe() {
        _selectedCafe.value = null
    }

    fun clearSelectedServerCafe() {
        _selectedServerCafe.value = null
    }
}