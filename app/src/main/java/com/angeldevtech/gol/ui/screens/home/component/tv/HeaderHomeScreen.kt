package com.angeldevtech.gol.ui.screens.home.component.tv

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.Coil
import com.angeldevtech.gol.R
import com.angeldevtech.gol.ui.screens.home.HomeUIState

@Composable
fun HeaderHomeScreen(
    uiState: HomeUIState,
    refresh: () -> Unit,
    refreshButtonFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var showSettingsDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "FakeOTT",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "~ yeah it's fake!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                onClick = { showSettingsDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }

            if (uiState is HomeUIState.Success) {
                Button(
                    onClick = refresh,
                    modifier = Modifier.focusRequester(refreshButtonFocusRequester)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.refresh)
                    )
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialogTV(
            onDismiss = { showSettingsDialog = false }
        )
    }
}

@Composable
fun SettingsDialogTV(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    BackHandler(onBack = onDismiss)

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.width(420.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = SurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Button(
                    onClick = {
                        try {
                            Coil.imageLoader(context).memoryCache?.clear()
                            Coil.imageLoader(context).diskCache?.clear()
                            Toast.makeText(context, context.getString(R.string.settings_cache_cleared), Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cleaning),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = stringResource(R.string.settings_clear_cache))
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.close))
                }

                Text(
                    text = stringResource(R.string.settings_version),
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}