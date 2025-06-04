package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun EventType(
    modifier: Modifier = Modifier,
    type: AgendaItemDetails,
){
    val colors = LocalExtendedColors.current

    val color = when(type){
        is AgendaItemDetails.Event -> colors.tertiary
        is AgendaItemDetails.Reminder -> colors.surfaceHigher
        is AgendaItemDetails.Task -> MaterialTheme.colorScheme.secondary
    }

    val text = when(type){
        is AgendaItemDetails.Event -> stringResource(id = R.string.event)
        is AgendaItemDetails.Reminder -> stringResource(id = R.string.reminder)
        is AgendaItemDetails.Task -> stringResource(id = R.string.task)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .size(20.dp)
                .background(color)
        )

        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun EventTypePreview(){
    TaskyTheme {
        EventType(
            type = AgendaItemDetails.Event()
        )
    }
}