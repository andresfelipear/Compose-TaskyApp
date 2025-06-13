package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.aarevalo.tasky.agenda.presentation.agenda_detail.AgendaItemDetails
import com.aarevalo.tasky.core.util.toInitials
import com.aarevalo.tasky.core.util.toTitleCase
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme
import com.example.ui.theme.LocalExtendedTypography

@Composable
fun AttendeeItem(
    modifier: Modifier = Modifier,
    attendee: Attendee,
    eventDetails: AgendaItemDetails.Event,
    isEditing: Boolean,
    onDeleteAttendee: (String) -> Unit,
){
    val typography = LocalExtendedTypography.current
    val colors = LocalExtendedColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = colors.surfaceHigher,
            )
            .padding(
                vertical = 7.dp,
                horizontal = 12.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = attendee.fullName.toInitials().uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = attendee.fullName.toTitleCase(),
            style = typography.headlineExtraSmall,
            color = MaterialTheme.colorScheme.primary
        )

        if(eventDetails.isUserEventCreator) {
            if(attendee.userId == eventDetails.eventCreator?.userId){
                Text(
                    text = stringResource(id = R.string.creator).uppercase(),
                    style = typography.labelExtraSmall,
                    color = colors.onSurfaceVariant70
                )
            } else {
                if(isEditing){
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = {
                            onDeleteAttendee(attendee.userId)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = stringResource(id = R.string.delete_attendee),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun AttendeeItemPreview(){
    TaskyTheme {
        AttendeeItem(
            attendee = Attendee(
                userId = "1",
                fullName = "Andres Arevalo",
                email = "john.c.breckinridge@altostrat.com",
                isGoing = true,
            ),
            eventDetails = AgendaItemDetails.Event(
                isUserEventCreator = true,
                eventCreator = Attendee(
                    userId = "3",
                    fullName = "John Doe",
                    email = "robert.cooper.grier@examplepetstore.com",
                    isGoing = true
                )
            ),
            isEditing = true,
            onDeleteAttendee = {}
        )
    }
}