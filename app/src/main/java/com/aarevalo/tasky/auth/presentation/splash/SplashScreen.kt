package com.aarevalo.tasky.auth.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aarevalo.tasky.R
import com.aarevalo.tasky.core.util.UiEvent
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun SplashScreenRoot(
    viewModel: SplashViewModel = hiltViewModel(),
    navController: NavController? = null
){

    LaunchedEffect(key1 = true) {
        viewModel.event.collect {
            when(it) {
                is UiEvent.Success -> {
                    TODO()
                }

                else -> Unit
            }
        }
    }

    SplashScreen()

}

@Composable
fun SplashScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = stringResource(id = R.string.app_name),
            tint = MaterialTheme.colorScheme.onBackground
            )
    }

}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun SplashScreenPreview() {
    TaskyTheme {
        SplashScreen()
    }

}