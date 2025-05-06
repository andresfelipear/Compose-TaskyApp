package com.aarevalo.tasky.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aarevalo.tasky.auth.presentation.components.ActionButton
import com.aarevalo.tasky.auth.presentation.components.InputTextField
import com.aarevalo.tasky.auth.presentation.components.PasswordTextField
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .clip(RoundedCornerShape(LocalSpacing.current.spaceLarge))
                            .padding(innerPadding)
                            .padding(vertical = 28.dp, horizontal = 16.dp),
                    ) {
                        ActionButton(
                            text = "GET STARTED",
                            onClick = {},
                            modifier = Modifier,
                            isLoading = false,
                            isEnabled = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ActionButton(
                            text = "LOGIN",
                            onClick = {},
                            modifier = Modifier,
                            isLoading = false,
                            isEnabled = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        InputTextField(
                            text = "",
                            onValueChange = {},
                            hint = "Name",
                            isValidInput = false
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        InputTextField(
                            text = "Aidan",
                            onValueChange = {},
                            hint = "Hint",
                            isValidInput = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PasswordTextField(
                            password = "123456",
                            onPasswordChange = {},
                            hint = "Password"
                        )
                    }

                }
            }
        }
    }
}
