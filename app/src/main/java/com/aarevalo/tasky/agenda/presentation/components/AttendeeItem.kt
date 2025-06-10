package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.aarevalo.tasky.core.util.toInitials
import com.aarevalo.tasky.core.util.toTitleCase
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.TaskyTheme
import com.example.ui.theme.LocalExtendedTypography

@Composable
fun AttendeeItem(
    modifier: Modifier = Modifier,
    fullName: String,
    isUserEventCreator: Boolean = false,
){
    val typography = LocalExtendedTypography.current
    val colors = LocalExtendedColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 7.dp,
                horizontal = 12.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = fullName.toInitials().uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = fullName.toTitleCase(),
            style = typography.headlineExtraSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        if(isUserEventCreator) {
            Text(
                text = stringResource(id = R.string.creator).uppercase(),
                style = typography.labelExtraSmall,
                color = colors.onSurfaceVariant70
            )
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun AttendeeItemPreview(){
    TaskyTheme {
        AttendeeItem(
            fullName = "John Doe",
            isUserEventCreator = true
        )
    }
}