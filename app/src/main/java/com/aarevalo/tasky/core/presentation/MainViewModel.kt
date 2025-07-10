package com.aarevalo.tasky.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionStorage: SessionStorage
): ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated
        .onStart {
            checkAuth()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _isCheckingAuth = MutableStateFlow(false)
    val isCheckingAuth = _isCheckingAuth.asStateFlow()

    private fun checkAuth() {
        viewModelScope.launch {
            _isCheckingAuth.update {
                true
            }
            _isAuthenticated.update {
                sessionStorage.getSession()?.accessToken.isNullOrBlank().not()
            }
            _isCheckingAuth.update {
                false
            }
        }
    }
}
