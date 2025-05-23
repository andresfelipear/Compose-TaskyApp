package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem
import com.aarevalo.tasky.core.presentation.components.TaskyDropDownMenu
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun AddAgendaItemButton(
    modifier: Modifier = Modifier,
    onClick: (TaskyDropDownMenuItem) -> Unit,
    dropDownMenuItems: List<TaskyDropDownMenuItem>
){
    val isContextMenuVisible = rememberSaveable{
        mutableStateOf(false)
    }

    FloatingActionButton(
        modifier = modifier
            .width(68.dp)
            .height(68.dp),
        onClick = {
            isContextMenuVisible.value = true
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        TaskyDropDownMenu(
            isContextMenuVisible = isContextMenuVisible,
            dropDownMenuItems = dropDownMenuItems,
            onItemClick = {
                onClick(it)
            }
        )

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 34)
fun AddAgendaItemButtonPreview(){
    TaskyTheme {
        AddAgendaItemButton(
            onClick = {},
            dropDownMenuItems = listOf(
                TaskyDropDownMenuItem(
                    text = "Item 1",
                ),
                TaskyDropDownMenuItem(
                    text = "Item 2",
                )
            )
        )
    }
}