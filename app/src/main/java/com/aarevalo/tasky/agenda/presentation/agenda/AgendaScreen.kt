package com.aarevalo.tasky.agenda.presentation.agenda

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun AgendaScreenRoute(
    viewModel: AgendaViewModel = hiltViewModel(),
    navController: NavController
){
    AgendaScreen()

}

@Composable
fun AgendaScreen(){
    Text(text = "Agenda Screen")
}