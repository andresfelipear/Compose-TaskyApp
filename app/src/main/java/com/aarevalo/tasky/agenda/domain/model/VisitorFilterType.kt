package com.aarevalo.tasky.agenda.domain.model

enum class VisitorFilterType {
    ALL,
    GOING,
    NOT_GOING;

    fun toHumanReadableString(): String {
        return when(this){
            VisitorFilterType.ALL -> "All"
            VisitorFilterType.GOING -> "Going"
            VisitorFilterType.NOT_GOING -> "Not Going"
        }
    }
}


