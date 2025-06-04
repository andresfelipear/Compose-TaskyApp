package com.aarevalo.tasky.agenda.presentation.edit_text

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.tasky.agenda.domain.model.EditTextFieldType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditTextViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val type = savedStateHandle.get<String>("type")?.let{
        EditTextFieldType.valueOf(it)
    } ?: throw IllegalArgumentException("Type is required")

    private val text = savedStateHandle.get<String>("text")?:""

    private val _state = MutableStateFlow(
        EditTextScreenState()
    )

    val state = _state
        .onStart {
            setType()
            setInitialText()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )


    private fun setType(){
        _state.update {
            it.copy(type = type)
        }
    }

    private fun setInitialText(){
        _state.update {
            it.copy(textFieldContent = TextFieldState(initialText = text))
        }
    }

}
