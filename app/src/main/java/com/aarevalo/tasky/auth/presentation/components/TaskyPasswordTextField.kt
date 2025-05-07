package com.aarevalo.tasky.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun TaskyPasswordTextField(
    passwordState: TextFieldState,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    hint: String = stringResource(id = R.string.password),
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurface,
    )
){
    val spacing = LocalSpacing.current
    val colors = LocalExtendedColors.current

    BasicSecureTextField(
        state = passwordState,
        textStyle = textStyle,
        textObfuscationMode = if (isPasswordVisible) TextObfuscationMode.Visible else TextObfuscationMode.Hidden,
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceHigher)
            .clip(RoundedCornerShape(10.dp))
            .padding(
                vertical = spacing.spaceMedium,
                horizontal = spacing.spaceExtraMedium
            ),
        decorator = { innerTextField ->
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier){
                    if (passwordState.text.isEmpty()) {
                        Text(
                            text = hint,
                            style = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    innerTextField()
                }
                IconButton(
                    onClick = {onPasswordVisibilityChange(isPasswordVisible)},
                    modifier = Modifier.padding(0.dp).size(20.dp)
                ) {
                    when (isPasswordVisible) {
                        true -> Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = stringResource(id = R.string.eye_open),
                            tint = colors.onSurfaceVariant70,
                            modifier = Modifier.size(20.dp).padding(0.dp)
                        )
                        false -> Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = stringResource(id = R.string.eye_close),
                            tint = colors.onSurfaceVariant70,
                            modifier = Modifier.size(20.dp).padding(0.dp)
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TaskyPasswordTextFieldPreview() {
    TaskyTheme {
        TaskyPasswordTextField(
            passwordState = TextFieldState(initialText = "12345"),
            isPasswordVisible = true,
            onPasswordVisibilityChange = {}
        )
    }
}