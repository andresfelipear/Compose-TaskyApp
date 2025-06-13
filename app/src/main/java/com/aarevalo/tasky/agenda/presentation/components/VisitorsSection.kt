package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.VisitorFilterType
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AttendeesState
import com.aarevalo.tasky.core.presentation.components.TaskyActionButton
import com.aarevalo.tasky.core.presentation.ui.asUiText
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDateTime

@Composable
fun VisitorsSection(
    modifier: Modifier = Modifier,
    isEditing: Boolean,
    attendeesState: AttendeesState,
    eventDetails: AgendaItemDetails.Event,
    onFilterTypeChanged: (VisitorFilterType) -> Unit,
    onDeleteAttendee: (String) -> Unit,
    onAddNewAttendee: () -> Unit,
) {
    val colors = LocalExtendedColors.current

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.visitor),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if(isEditing){
                IconButton(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(4.dp)
                        )
                        .background(color = colors.surfaceHigher)
                        .size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = colors.surfaceHigher,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    onClick = {
                        onAddNewAttendee()
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(0)),
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_new_attendee)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            VisitorFilterType.entries.forEach { filter ->
                var buttonColors = ButtonColors(
                    containerColor = colors.surfaceHigher,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = colors.onSurfaceVariant70,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                )
                if(filter == attendeesState.selectedFilter){
                    buttonColors = buttonColors.copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                TaskyActionButton(
                    modifier = Modifier
                        .weight(1f),
                    onClick = { onFilterTypeChanged(filter) },
                    colors = buttonColors,
                    text = filter.asUiText().asString(),
                    textStyle = MaterialTheme.typography.labelSmall,
                    verticalPadding = 5.dp,
                )
            }
        }

        if(eventDetails.attendees.isNotEmpty()){

            if(attendeesState.selectedFilter == VisitorFilterType.ALL || attendeesState.selectedFilter == VisitorFilterType.GOING){
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ){
                    Text(
                        text = stringResource(id = R.string.going),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        attendeesState.going.forEach { attendee ->
                            AttendeeItem(
                                attendee = attendee,
                                eventDetails = eventDetails,
                                isEditing = isEditing,
                                onDeleteAttendee = onDeleteAttendee
                            )
                        }
                    }
                }
            }

            if(attendeesState.selectedFilter == VisitorFilterType.ALL || attendeesState.selectedFilter == VisitorFilterType.NOT_GOING){
                if(attendeesState.notGoing.isNotEmpty()){
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ){
                        Text(
                            text = stringResource(id = R.string.not_going),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            attendeesState.notGoing.forEach{ attendee ->
                                AttendeeItem(
                                    attendee = attendee,
                                    eventDetails = eventDetails,
                                    isEditing = isEditing,
                                    onDeleteAttendee = onDeleteAttendee
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun VisitorsSectionPreview(){
    TaskyTheme {
        VisitorsSection(
            isEditing = true,
            onFilterTypeChanged = {},
            onDeleteAttendee = {},
            eventDetails = AgendaItemDetails.Event(
                attendees = listOf(
                    Attendee(
                        userId = "1",
                        fullName = "John Doe",
                        email = "james.monroe@examplepetstore.com",
                        isGoing = true,
                        reminderAt = LocalDateTime.now()
                    ),
                    Attendee(
                        userId = "2",
                        fullName = "Jane Doe",
                        email = "john.mckinley@examplepetstore.com",
                        isGoing = false,
                        reminderAt = LocalDateTime.now()
                    ),
                    Attendee(
                        userId = "3",
                        fullName = "Andres Arevalo",
                        email = "john.mckinley@examplepetstore.com",
                        isGoing = false,
                        reminderAt = LocalDateTime.now()
                    ),
                ),
                isUserEventCreator = true,
                eventCreator = Attendee(
                    userId = "1",
                    fullName = "John Doe",
                    email = "john.mclean@examplepetstore.com",
                    isGoing = true,
                    reminderAt = LocalDateTime.now()
                ),
            ),
            onAddNewAttendee = {},
            attendeesState = AttendeesState()
        )
    }
}