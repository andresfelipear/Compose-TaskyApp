package com.aarevalo.tasky.agenda.presentation.agenda

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aarevalo.tasky.agenda.presentation.components.AgendaScreenHeader
import com.aarevalo.tasky.agenda.presentation.components.CustomDatePicker
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreenRoute(
    viewModel: AgendaViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    if(state.showDatePicker){
        CustomDatePicker(
            datePickerState = state.datePickerState,
            onDateChanged = { date ->
                viewModel.onAction(AgendaScreenAction.OnDateChanged(date))
            },
            onShowDatePicker = { showDatePicker ->
                viewModel.onAction(AgendaScreenAction.OnShowDatePicker(showDatePicker))
            }
        )
    }

    AgendaScreen(
        state = state,
        onAction = viewModel::onAction
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgendaScreen(
    onAction: (AgendaScreenAction) -> Unit = {},
    state: AgendaScreenState
) {

    val spacing = LocalSpacing.current
    val snackBarState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackBarState)
    },
             floatingActionButton = {
                 FloatingActionButton(
                     onClick = { /*TODO*/ },
                     modifier = Modifier
                         .width(68.dp)
                         .height(68.dp),
                     containerColor = MaterialTheme.colorScheme.primary,
                     contentColor = MaterialTheme.colorScheme.onPrimary,
                 ) {
                     Icon(
                         imageVector = Icons.Default.Add,
                         contentDescription = "Add",
                         modifier = Modifier.size(24.dp)
                     )
                 }
             }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                AgendaScreenHeader(
                    month = state.date.month.toString()
                        .uppercase(),
                    initials = state.initials,
                    onOpenCalendar = {
                        onAction(AgendaScreenAction.OnShowDatePicker(true))
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 24.dp
                            )
                        )
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(
                            horizontal = spacing.spaceMedium,
                            vertical = spacing.spaceLarge
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {

                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(
    showBackground = true,
    apiLevel = 34
)
fun AgendaScreenPreview() {
    TaskyTheme {
        AgendaScreen(
            state = AgendaScreenState()
        )
    }
}
