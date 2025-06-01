package com.aarevalo.tasky.core.domain.dropdownMenu

data class TaskyDropDownMenuItem(
    val onClick: () -> Unit = {},
    val text: String,
)
