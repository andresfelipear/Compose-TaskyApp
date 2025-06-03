package com.aarevalo.tasky.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem

private const val OFFSET_Y = -20

@Composable
fun TaskyDropDownMenu(
    modifier: Modifier = Modifier,
    isContextMenuVisible: Boolean,
    onDismissRequest: () -> Unit,
    dropDownMenuItems: List<TaskyDropDownMenuItem>,
    extraOffset: Int = 0,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    DropdownMenu(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface),
        expanded = isContextMenuVisible,
        onDismissRequest = {
            onDismissRequest()
        },
        offset = DpOffset.Zero.copy(
            y = OFFSET_Y.dp + extraOffset.dp
        )
    ) {
        dropDownMenuItems.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    item.onClick()
                    onDismissRequest()
                },
                text = {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        text = item.text
                    )
                },
            )
        }
    }
}