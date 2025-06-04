package com.aarevalo.tasky.agenda.presentation.photo_preview

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.aarevalo.tasky.R
import com.aarevalo.tasky.agenda.domain.model.EventPhoto
import com.aarevalo.tasky.core.navigation.Destination
import com.aarevalo.tasky.core.presentation.components.AppBar
import com.aarevalo.tasky.ui.theme.TaskyTheme

@Composable
fun PhotoPreviewScreenRoot(
    route: Destination.Route.PhotoPreviewRoute,
    navController: NavController
){
    val photo = if(Uri.parse(route.photoUri).scheme?.startsWith("content") == true){
        EventPhoto.Local(key = route.photoKey, uriString = route.photoUri)
    } else {
        EventPhoto.Remote(key = route.photoKey, photoUrl = route.photoUri)
    }

    PhotoPreviewScreen(
        isEditable = route.isEditable,
        photo = photo,
        onAction = { action ->
            when(action) {
                is PhotoPreviewAction.GoBack -> {
                    navController
                        .previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("photo_preview_result", PhotoPreviewResult(
                            photoId = action.photoId,
                            wasPhotoDeleted = action.wasDeleted
                        ))
                    navController.navigateUp()
                }
            }
        }
    )

}

@Composable
fun PhotoPreviewScreen(
    isEditable: Boolean,
    photo: EventPhoto,
    onAction: (PhotoPreviewAction) -> Unit
){
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { AppBar(
            contentStart = {
                IconButton(
                    onClick = {
                        onAction(PhotoPreviewAction.GoBack(
                            photoId = photo.key(),
                            wasDeleted = false
                        ))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.go_back),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            contentMiddle = {
                Text(
                    text = stringResource(id = R.string.photo_preview_title),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                )
            },
            contentEnd = {
                if(isEditable) {
                    IconButton(
                        onClick = {
                            onAction(PhotoPreviewAction.GoBack(
                                photoId = photo.key(),
                                wasDeleted = true
                            ))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete_photo),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        )
        }
    ){ contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.background
                )
                .padding(
                    top = 64.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(
                        width = 333.dp,
                        height = 555.dp
                    )
                    .clip(
                        RoundedCornerShape(5.dp)
                    ),
                painter = rememberAsyncImagePainter(
                    model = photo.uri(),
                    error = painterResource(id = R.drawable.ic_launcher_background),
                    placeholder = painterResource(id = R.drawable.ic_launcher_background)
                ),
                contentDescription = stringResource(id = R.string.photo_preview_image),
                contentScale = ContentScale.Crop,
            )


        }
    }

}

@Preview(showBackground = true, apiLevel = 33)
@Composable
fun PhotoPreviewScreenPreview(){
    TaskyTheme {
        PhotoPreviewScreen(
            isEditable = true,
            photo = EventPhoto.Local(key = "", uriString = ""),
            onAction = {}
        )
    }
}