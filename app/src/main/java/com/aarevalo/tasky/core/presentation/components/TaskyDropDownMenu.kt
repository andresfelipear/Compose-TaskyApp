package com.aarevalo.tasky.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem


@Composable
fun TaskyDropDownMenu(
    modifier: Modifier = Modifier,
    isContextMenuVisible: Boolean,
    onDismissRequest: () -> Unit,
    dropDownMenuItems: List<TaskyDropDownMenuItem>,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    DropdownMenu(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .heightIn(min = 0.dp)
            .padding(0.dp),
        expanded = isContextMenuVisible,
        onDismissRequest = {
            onDismissRequest()
        },
        offset = DpOffset(
            y = offsetY,
            x = offsetX
        ),
    ) {
        dropDownMenuItems.forEach { item ->
            DropdownMenuItem(
                modifier = Modifier.heightIn(min = 0.dp)
                    .padding(0.dp),
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
                contentPadding = PaddingValues(
                    vertical = 0.dp,
                    horizontal = 16.dp
                )
            )
        }
    }
}