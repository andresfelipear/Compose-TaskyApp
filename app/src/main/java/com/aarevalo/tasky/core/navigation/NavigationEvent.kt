package com.aarevalo.tasky.core.navigation

/**
 * Centralized navigation events that bubble up to the navigation host.
 * All navigation logic is handled at the MainActivity level, not in individual screens.
 */
sealed interface NavigationEvent {
    
    // Auth Navigation
    data object NavigateToLogin : NavigationEvent
    data object NavigateToRegister : NavigationEvent
    data object NavigateToAgenda : NavigationEvent
    
    // Agenda Navigation
    data class NavigateToAgendaDetail(
        val agendaItemId: String?,
        val isEditable: Boolean,
        val type: String
    ) : NavigationEvent
    
    data class NavigateToEditText(
        val type: String,
        val text: String
    ) : NavigationEvent
    
    data class NavigateToPhotoPreview(
        val photoUri: String,
        val photoKey: String,
        val isEditable: Boolean
    ) : NavigationEvent
    
    // Navigation actions
    data object NavigateBack : NavigationEvent
    data object NavigateUp : NavigationEvent
}

