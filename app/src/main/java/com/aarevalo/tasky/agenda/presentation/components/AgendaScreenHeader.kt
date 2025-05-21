package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun AgendaScreenHeader(
    month: String,
    fullname: String,
    modifier: Modifier = Modifier,
    onOpenCalendar: () -> Unit = {},
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .padding(
                horizontal = spacing.spaceMedium,
                vertical = spacing.spaceSmall
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = month,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
            )
            IconButton(
                onClick = { onOpenCalendar() }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "arrow down",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { onOpenCalendar() }
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "arrow down",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

    }
}

@Composable
@Preview(showBackground = true, apiLevel = 34)
fun AgendaScreenHeaderPreview() {
    TaskyTheme {
        AgendaScreenHeader(
            month = "January",
            fullname = "John Doe",
        )
    }
}