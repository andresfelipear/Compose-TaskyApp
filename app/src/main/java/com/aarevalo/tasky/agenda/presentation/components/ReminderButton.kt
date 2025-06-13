package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.ReminderType
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem
import com.aarevalo.tasky.core.presentation.components.TaskyDropDownMenu
import com.aarevalo.tasky.core.util.UiText
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun ReminderButton(
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    dropDownMenuItems: List<TaskyDropDownMenuItem>,
    reminderType: ReminderType
){
    val colors = LocalExtendedColors.current

    var isContextMenuVisible by rememberSaveable{
        mutableStateOf(false)
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ){
        Button(
            onClick = {
                if(isEditable){
                    isContextMenuVisible = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = colors.surfaceHigher,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                },
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 20.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = {
                        isContextMenuVisible = true
                    },
                    enabled = isEditable,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.surfaceHigher,
                        contentColor = colors.onSurfaceVariant70
                    )
                ){
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = stringResource(id = R.string.reminder),
                        tint = colors.onSurfaceVariant70
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.reminder_type, reminderType.duration.UiText().asString()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if(isEditable){
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(id = R.string.dropdown),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        TaskyDropDownMenu(
            isContextMenuVisible = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            dropDownMenuItems = dropDownMenuItems,
        )
    }
}


@Preview(showBackground = true, apiLevel = 34)
@Composable
fun ReminderButtonPreview(){
    TaskyTheme {
        ReminderButton(
            isEditable = true,
            dropDownMenuItems = listOf(
                TaskyDropDownMenuItem(
                    text = "Item 1",
                ),
            ),
            reminderType = ReminderType.ONE_HOUR
        )
    }
}