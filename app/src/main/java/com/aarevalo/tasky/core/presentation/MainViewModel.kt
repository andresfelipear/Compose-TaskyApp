package com.aarevalo.tasky.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _isCheckingAuth = MutableStateFlow(true)
    val isCheckingAuth = _isCheckingAuth.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000)
            _isCheckingAuth.value = false
            _isAuthenticated.value = true
        }
    }

}
