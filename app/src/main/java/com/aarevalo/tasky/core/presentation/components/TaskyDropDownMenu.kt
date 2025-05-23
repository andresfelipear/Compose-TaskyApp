package com.aarevalo.tasky.core.presentation.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem


@Composable
fun TaskyDropDownMenu(
    modifier: Modifier = Modifier,
    isContextMenuVisible: MutableState<Boolean>,
    dropDownMenuItems: List<TaskyDropDownMenuItem>,
    onItemClick: (TaskyDropDownMenuItem) -> Unit = {}
) {
    DropdownMenu(
        modifier = modifier,
        expanded = isContextMenuVisible.value,
        onDismissRequest = { isContextMenuVisible.value = false },
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        dropDownMenuItems.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    onItemClick(item)
                    isContextMenuVisible.value = false
                },
                text = {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        text = item.text
                    )
                },
            )
        }
    }
}