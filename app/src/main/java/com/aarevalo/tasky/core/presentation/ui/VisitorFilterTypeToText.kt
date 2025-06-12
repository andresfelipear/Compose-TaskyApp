package com.aarevalo.tasky.core.presentation.ui

import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.VisitorFilterType
import com.aarevalo.tasky.core.presentation.util.UiText

fun VisitorFilterType.asUiText(): UiText {
    return when (this){
        VisitorFilterType.ALL -> UiText.StringResource(R.string.all)
        VisitorFilterType.GOING -> UiText.StringResource(R.string.going)
        VisitorFilterType.NOT_GOING -> UiText.StringResource(R.string.not_going)
    }
}