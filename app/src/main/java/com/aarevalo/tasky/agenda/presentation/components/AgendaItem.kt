package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem
import com.aarevalo.tasky.core.presentation.components.TaskyDropDownMenu
import com.aarevalo.tasky.core.util.formattedDateTimeToString
import com.aarevalo.tasky.core.util.formattedFromToDateTimeToString
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

@Composable
fun AgendaItemComponent(
    modifier: Modifier = Modifier,
    agendaItem: AgendaItem,
    dropDownMenuItems: List<TaskyDropDownMenuItem>,
    onClick : (agendaItemId: String) -> Unit
) {
    val colors = LocalExtendedColors.current

    val textColor = when(agendaItem.details) {
        is AgendaItemDetails.Task -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.primary
    }

    val titleTextDecoration = when(agendaItem.details) {
        is AgendaItemDetails.Task -> {
            if(agendaItem.details.isDone) TextDecoration.LineThrough else TextDecoration.None
        }
        else -> TextDecoration.None
    }

    val color = when(agendaItem.details) {
        is AgendaItemDetails.Event -> colors.tertiary
        is AgendaItemDetails.Task -> MaterialTheme.colorScheme.secondary
        is AgendaItemDetails.Reminder -> colors.surfaceHigher
    }

    val textDateTime = when(agendaItem.details) {
        is AgendaItemDetails.Event -> {
            formattedFromToDateTimeToString(
                dateFrom = agendaItem.fromDate,
                timeFrom = agendaItem.fromTime,
                dateTo = agendaItem.details.toDate,
                timeTo = agendaItem.details.toTime
            )
        }

        else -> formattedDateTimeToString(
            date = agendaItem.fromDate,
            time = agendaItem.fromTime
        )
    }

    var isContextMenuVisible by rememberSaveable{
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                MaterialTheme.shapes.large
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
                       onClick = {
                           if(agendaItem.details is AgendaItemDetails.Task){
                               onClick(agendaItem.id)
                           }
                       }) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = if(agendaItem.details is AgendaItemDetails.Task && agendaItem.details.isDone) Icons.Default.CheckCircleOutline else Icons.Default.RadioButtonUnchecked,
                    contentDescription = stringResource(id = R.string.done),
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
                        .defaultMinSize(minHeight = 64.dp)
                        .fillMaxWidth(),
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

            IconButton(
                modifier = Modifier.width(40.dp),
                onClick = {
                    isContextMenuVisible = true
                }) {

                TaskyDropDownMenu(
                    isContextMenuVisible = isContextMenuVisible,
                    dropDownMenuItems = dropDownMenuItems,
                    onDismissRequest = {
                        isContextMenuVisible = false
                    },
                )

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
                details = AgendaItemDetails.Task(),
                fromTime = LocalTime.now(),
                fromDate = LocalDate.now(),
                remindAt = ZonedDateTime.now(),
                hostId = ""
                ),
            dropDownMenuItems = listOf(
                TaskyDropDownMenuItem(
                    text = "Edit",
                    onClick = { /*TODO*/ }
            ),
        ),
            onClick = {}
        )
    }
}