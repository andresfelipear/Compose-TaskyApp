package com.aarevalo.tasky.core.presentation.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.Dp
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
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    colors: ButtonColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = LocalExtendedColors.current.onSurfaceVariant70,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
    ),
    border: BorderStroke? = null,
    verticalPadding: Dp = LocalSpacing.current.spaceMedium
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = isEnabled,
        colors = colors,
        shape = RoundedCornerShape(38.dp),
        border = border
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(vertical = verticalPadding)
                )
            }
            else -> {
                Text(
                    text = text,
                    style = textStyle.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = verticalPadding),
                )
            }

        }
    }

}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TaskyActionButtonPreview() {
    TaskyTheme {
        TaskyActionButton(text = "GET STARTED", onClick = {}, isLoading = false, isEnabled = true)
    }
}