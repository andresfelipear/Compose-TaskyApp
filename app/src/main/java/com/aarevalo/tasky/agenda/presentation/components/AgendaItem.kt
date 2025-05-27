package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.agenda.domain.AgendaItem
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.util.toShortDateTime
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun AgendaItemComponent(
    modifier: Modifier = Modifier,
    agendaItem: AgendaItem,
) {
    val colors = LocalExtendedColors.current

    val textColor = when(agendaItem.details) {
        is AgendaItemDetails.Task -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.primary
    }

    val titleTextDecoration = when(agendaItem.details) {
        is AgendaItemDetails.Task -> TextDecoration.LineThrough
        else -> TextDecoration.None
    }

    val color = when(agendaItem.details) {
        is AgendaItemDetails.Event -> colors.tertiary
        is AgendaItemDetails.Task -> MaterialTheme.colorScheme.secondary
        is AgendaItemDetails.Reminder -> colors.surfaceHigher
    }

    val textDateTime = when(agendaItem.details) {
        is AgendaItemDetails.Event -> {
            "".toShortDateTime(
                agendaItem.fromDate,
                agendaItem.fromTime
            ) + " - " + "".toShortDateTime(
                agendaItem.details.toDate,
                agendaItem.details.toTime
            )
        }

        else -> "".toShortDateTime(
            agendaItem.fromDate,
            agendaItem.fromTime
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(
                color = color,
            )
            .padding(
                bottom = 16.dp,
                top = 8.dp
            )
    ) {
        Row(
            modifier = modifier
        ) {
            IconButton(modifier = Modifier.width(40.dp),
                       onClick = { /*TODO*/ }) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = if(agendaItem.details is AgendaItemDetails.Task && agendaItem.details.isDone) Icons.Default.CheckCircleOutline else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Mark as done",
                    tint = textColor
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        top = 12.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = agendaItem.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            textDecoration = titleTextDecoration
                        ),
                        color = textColor
                    )

                    Text(
                        modifier = Modifier,
                        text = agendaItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )

                }
            }

            IconButton(modifier = Modifier.width(40.dp),
                       onClick = { /*TODO*/ }) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More Horizontal",
                    tint = textColor
                )
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp),
            textAlign = TextAlign.End,
            text = textDateTime,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }


}

@ExperimentalMaterial3Api
@Composable
@Preview(
    showBackground = true,
    apiLevel = 34
)
fun AgendaItemPreview() {
    TaskyTheme {
        AgendaItemComponent(
            agendaItem = AgendaItem(
                id = "1",
                title = "Event title",
                description = "Event description",
                details = AgendaItemDetails.Task()
            )
        )
    }
}