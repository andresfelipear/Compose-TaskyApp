package com.aarevalo.tasky.agenda.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDate

@Composable
fun AgendaScreenHeader(
    month: String,
    initials: String,
    modifier: Modifier = Modifier,
    onOpenCalendar: () -> Unit = {},
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(
                horizontal = spacing.spaceMedium,
                vertical = spacing.spaceSmall
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = month.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
            )
            IconButton(
                onClick = { onOpenCalendar() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "arrow down",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onOpenCalendar() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar_today),
                    contentDescription = "arrow down",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

            AvatarIcon(
                initials = initials,
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true, apiLevel = 34)
fun AgendaScreenHeaderPreview() {
    TaskyTheme {
        AgendaScreenHeader(
            month = LocalDate.now().month.toString(),
            initials = "jd",
        )
    }
}