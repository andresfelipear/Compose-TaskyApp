package com.aarevalo.tasky.agenda.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.core.presentation.components.TaskyActionButton
import com.aarevalo.tasky.core.presentation.components.TaskyInputTextField
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun AddAttendeeDialog(
    modifier: Modifier = Modifier,
    showConfirmationDialog: Boolean,
    onAddAttendee: (String) -> Unit,
    onDismissConfirmationDialog: () -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    isValidEmail: Boolean,
    isAddingAttendee: Boolean,
){
    val colors = LocalExtendedColors.current
    val spacing = LocalSpacing.current

    if(showConfirmationDialog){
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissConfirmationDialog,
            title = {
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = 8.dp,
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.add_attendee_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    )

                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = onDismissConfirmationDialog
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            text = {
                TaskyInputTextField(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(10.dp)
                        ),
                    text = email,
                    onValueChange = onEmailChange,
                    hint = stringResource(id = R.string.email_hint),
                )
            },
            confirmButton = {
                TaskyActionButton(
                    text = stringResource(id = R.string.add).uppercase(),
                    onClick = {
                        onAddAttendee(email)
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = colors.onSurfaceVariant70
                    ),
                    verticalPadding = spacing.spaceMedium,
                    isEnabled = isValidEmail,
                    isLoading = isAddingAttendee
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun AddAttendeeDialogPreview(){
    TaskyTheme {
        AddAttendeeDialog(
            showConfirmationDialog = true,
            onAddAttendee = {},
            onDismissConfirmationDialog = {},
            email = "",
            onEmailChange = {},
            isValidEmail = false,
            isAddingAttendee = false,
        )
    }
}