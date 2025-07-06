package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.AgendaItem
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenAction
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenState
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.domain.dropdownMenu.TaskyDropDownMenuItem
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

@Composable
fun AgendaList(
    modifier: Modifier = Modifier,
    state: AgendaScreenState,
    onAction: (AgendaScreenAction) -> Unit
){
    val sortedAgendaItems = state.agendaItems.sortedBy { it.fromTime }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val insertIndex = sortedAgendaItems.indexOfFirst { it.fromTime.isAfter(state.timeNeedled)}

        sortedAgendaItems.forEachIndexed { index, agendaItem ->
            if (index == insertIndex) {
                item(key = "time_needle") {
                    TimeNeedle(
                        modifier = if (index == 0){
                            Modifier.padding(top = 12.dp)
                        } else {
                            Modifier
                        }
                    )
                }
            }

            item(key = agendaItem.id) {
                AgendaItemComponent(
                    agendaItem = agendaItem,
                    modifier = Modifier.fillMaxWidth(),
                    dropDownMenuItems = listOf(
                        TaskyDropDownMenuItem(
                            text = stringResource(R.string.open),
                            onClick = {
                                onAction(
                                    AgendaScreenAction.OnOpenAgendaItemClick(
                                        agendaItemId = agendaItem.id,
                                        type = agendaItem.details
                                    )
                                )
                            }
                        ),
                        TaskyDropDownMenuItem(
                            text = stringResource(R.string.edit),
                            onClick = {
                                onAction(
                                    AgendaScreenAction.OnEditAgendaItemClick(
                                        agendaItemId = agendaItem.id,
                                        type = agendaItem.details
                                    )
                                )
                            }
                        ),
                        TaskyDropDownMenuItem(
                            text = stringResource(R.string.delete),
                            onClick = {
                                onAction(
                                    AgendaScreenAction.OnConfirmDeleteAgendaItem(
                                        agendaItemId = agendaItem.id,
                                        type = agendaItem.details
                                    )
                                )
                            }
                        )
                    )
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true, apiLevel = 34)
fun AgendaListPreview(){
    TaskyTheme {
        AgendaList(
            state = AgendaScreenState(
                agendaItems = listOf(
                    AgendaItem(
                        id = "1",
                        title = "Event 1",
                        description = "Event 1 description",
                        fromTime = LocalTime.now()
                            .plusMinutes(31),
                        fromDate = LocalDate.now(),
                        details = AgendaItemDetails.Event(),
                        remindAt = ZonedDateTime.now(),
                        hostId = ""
                    ),
                    AgendaItem(
                        id = "2",
                        title = "Event 2",
                        description = "Event 2 description",
                        fromTime = LocalTime.now()
                            .plusMinutes(32),
                        details = AgendaItemDetails.Task(),
                        fromDate = LocalDate.now(),
                        remindAt = ZonedDateTime.now(),
                        hostId = ""
                    ),
                    AgendaItem(
                        id = "3",
                        title = "Event 1",
                        description = "Event 1 description",
                        fromTime = LocalTime.now()
                            .plusMinutes(33),
                        fromDate = LocalDate.now(),
                        details = AgendaItemDetails.Event(),
                        remindAt = ZonedDateTime.now(),
                        hostId = ""
                    ),
                    AgendaItem(
                        id = "4",
                        title = "Event 2",
                        description = "Event 2 description",
                        fromTime = LocalTime.now()
                            .plusMinutes(0),
                        details = AgendaItemDetails.Reminder,
                        fromDate = LocalDate.now(),
                        remindAt = ZonedDateTime.now(),
                        hostId = ""
                    )
                ),
                selectedDate = LocalDate.now(),
            ),
            onAction = TODO(),
        )
    }
}