package com.aarevalo.tasky.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.R
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    hint: String = stringResource(id = R.string.password),
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurface,
    )
){
    val spacing = LocalSpacing.current
    val colors = LocalExtendedColors.current
    var passwordVisible by remember { mutableStateOf(false) }

    BasicTextField(
        value = password,
        onValueChange = onPasswordChange,
        textStyle = textStyle,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceHigher)
            .clip(RoundedCornerShape(10.dp))
            .padding(
                vertical = spacing.spaceMedium,
                horizontal = spacing.spaceExtraMedium
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier){
                    if (password.isEmpty()) {
                        Text(
                            text = hint,
                            style = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    innerTextField()
                }
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.padding(0.dp).size(20.dp)
                ) {
                    when (passwordVisible) {
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
fun PasswordTextFieldPreview() {
    TaskyTheme {
        PasswordTextField(
            password = "",
            onPasswordChange = {},
        )
    }
}