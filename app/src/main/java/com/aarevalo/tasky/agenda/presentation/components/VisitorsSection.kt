package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.Attendee
import com.aarevalo.tasky.agenda.domain.model.VisitorFilterType
import com.aarevalo.tasky.auth.presentation.components.TaskyActionButton
import com.aarevalo.tasky.core.util.toTitleCase
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun VisitorsSection(
    modifier: Modifier = Modifier,
    attendees: List<Attendee>,
    filterType: VisitorFilterType,
    isEditing: Boolean,
    onFilterTypeChanged: (VisitorFilterType) -> Unit,
) {
    val colors = LocalExtendedColors.current

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 8.dp
                )
        ) {
            Text(
                text = stringResource(id = R.string.visitor),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
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
                if(filter == filterType){
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
                    text = filter.toHumanReadableString(),
                    textStyle = MaterialTheme.typography.labelSmall,
                    verticalPadding = 5.dp,
                )
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun VisitorsSectionPreview(){
    TaskyTheme {
        VisitorsSection(
            attendees = emptyList(),
            filterType = VisitorFilterType.ALL,
            isEditing = false,
            onFilterTypeChanged = {}
        )
    }
}