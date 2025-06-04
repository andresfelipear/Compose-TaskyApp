package com.aarevalo.tasky.agenda.presentation.agenda_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.presentation.agenda.AgendaScreenAction
import com.aarevalo.tasky.agenda.presentation.components.AgendaList
import com.aarevalo.tasky.agenda.presentation.components.CalendarDaysSelector
import com.aarevalo.tasky.core.presentation.components.AppBar
import com.aarevalo.tasky.core.util.toTitleCase
import com.aarevalo.tasky.ui.theme.LocalExtendedColors
import com.aarevalo.tasky.ui.theme.LocalSpacing
import com.aarevalo.tasky.ui.theme.TaskyTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AgendaDetailScreenRoot(
    navController: NavController,
    viewModel: AgendaDetailViewModel = hiltViewModel(),
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    AgendaDetailScreen(
        state = state,
        onAction = {

        }
    )
}

@Composable
fun AgendaDetailScreen(
    state: AgendaDetailScreenState,
    onAction: (AgendaDetailScreenAction) -> Unit
){
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = LocalExtendedColors.current
    val spacing = LocalSpacing.current

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            AppBar(
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentStart = {
                    Text(
                        modifier = Modifier.clickable {
                            /* TODO */
                        },
                        text = stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 12.sp,
                            letterSpacing = 0.sp
                        )
                    )
                },
                contentMiddle = {
                    Text(
                        text = stringResource(
                            id = R.string.detail_screen_title, "EVENT"
                        ).uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                contentEnd = {
                    Text(
                        modifier = Modifier.clickable {
                            /* TODO */
                        },
                        text = stringResource(id = R.string.save),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colors.success,
                            lineHeight = 12.sp,
                            letterSpacing = 0.sp
                        )
                    )
                })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {



            }
        }
    }
}


@Preview(showBackground = true, apiLevel = 34)
@Composable
fun AgendaDetailScreenPreview(){
    TaskyTheme {
        AgendaDetailScreen(
            state = AgendaDetailScreenState(),
            onAction = {}
        )
    }
}