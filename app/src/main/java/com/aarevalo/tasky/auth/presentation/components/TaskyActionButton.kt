package com.aarevalo.tasky.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun TaskyActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = isEnabled,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = LocalExtendedColors.current.onSurfaceVariant70,
            disabledContentColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(38.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(vertical = LocalSpacing.current.spaceMedium)
                )
            }
            else -> {
                Text(
                    text = text,
                    style =textStyle.copy(textAlign = TextAlign.Center),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = LocalSpacing.current.spaceMedium),
                )
            }

        }
    }

}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TaskyActionButtonPreview() {
    TaskyTheme {
        TaskyActionButton(text = "GET STARTED", onClick = {}, isLoading = true, isEnabled = true)
    }
}