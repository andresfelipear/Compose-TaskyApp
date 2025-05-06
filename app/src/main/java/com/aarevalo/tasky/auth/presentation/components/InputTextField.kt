package com.aarevalo.tasky.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing

@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit,
    isValidInput: Boolean = false,
    hint: String = "",
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurface
    ),
) {
    val spacing = LocalSpacing.current
    val colors = LocalExtendedColors.current

    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = textStyle,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceHigher)
            .clip(RoundedCornerShape(10.dp))
            .padding(vertical = spacing.spaceMedium, horizontal = spacing.spaceExtraMedium),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier){
                    if (text.isEmpty()) {
                        Text(
                            text = hint,
                            style = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    innerTextField()
                }
                if(isValidInput){
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.check_icon),
                        tint = colors.success,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    )
}

@Preview(
    showBackground = true,
    apiLevel = 34
)
@Composable
fun InputTextFieldPreview() {
    InputTextField(
        text = "Name",
        onValueChange = {},
        hint = "Hint",
        isValidInput = true
    )
}