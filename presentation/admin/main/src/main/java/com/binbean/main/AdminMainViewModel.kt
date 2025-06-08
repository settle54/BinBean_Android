package com.binbean.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _cafeId = MutableStateFlow(-1)
    val cafeId: StateFlow<Int> = _cafeId.asStateFlow()

    init {
        loadCafeId()
    }

    private fun loadCafeId() {
        val prefs = context.getSharedPreferences("binbean_prefs", Context.MODE_PRIVATE)
        _cafeId.value = prefs.getInt("cafeId", -1)
    }

    fun getCafeId(): Int {
        return _cafeId.value
    }

    companion object {
        private const val TAG = "AdminMainViewModel"
    }
}