package com.aarevalo.tasky.agenda.domain.model

enum class VisitorFilterType {
    ALL,
    GOING,
    NOT_GOING;

    fun toHumanReadableString(): String {
        return when(this){
           ALL -> "All"
            GOING -> "Going"
            NOT_GOING -> "Not Going"
        }
    }
}


