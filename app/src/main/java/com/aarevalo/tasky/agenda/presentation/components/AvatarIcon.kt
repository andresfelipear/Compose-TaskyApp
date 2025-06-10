package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem
import com.aarevalo.tasky.core.presentation.components.TaskyDropDownMenu
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun AvatarIcon(
    modifier: Modifier = Modifier,
    initials: String,
    dropDownMenuItems: List<TaskyDropDownMenuItem>
){
    var isContextMenuVisible by rememberSaveable{
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable {
                isContextMenuVisible = true
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TaskyDropDownMenu(
            modifier = Modifier,
            isContextMenuVisible = isContextMenuVisible,
            dropDownMenuItems = dropDownMenuItems,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offsetY = 10.dp,
            textColor = MaterialTheme.colorScheme.error,
        )

        Text(
            text = initials.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 34)
fun AvatarIconPreview(){
    TaskyTheme {
        AvatarIcon(
            initials = "jd",
            dropDownMenuItems = listOf()
        )
    }
}