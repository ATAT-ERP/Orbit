package com.example.orbit.ui.cloud

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.orbit.dto.CloudFileDto
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CloudScreen(viewModel: CloudViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        viewModel.download.collect { event ->
            when (event) {
                is CloudDownload.Ready -> {
                    enqueueDownload(context, event.url, event.fileName)
                    snackbarHostState.showSnackbar("Descargando ${event.fileName}")
                }
                is CloudDownload.Failed -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is CloudUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is CloudUiState.Empty -> {
                    Text("Esta empresa todavía no tiene archivos.")
                }

                is CloudUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.retry() }) {
                            Text("Reintentar")
                        }
                    }
                }

                is CloudUiState.Content -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(state.files) { file: CloudFileDto ->
                            ListItem(
                                headlineContent = { Text(file.name) },
                                supportingContent = {
                                    Text("${formatSize(file.size)} · ${formatDate(file.createdAt)}")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (needsStoragePermission(context)) {
                                            permissionLauncher.launch(
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                                            )
                                        } else {
                                            viewModel.downloadFile(file)
                                        }
                                    }
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun formatDate(createdAt: String): String {
    return OffsetDateTime.parse(createdAt).format(dateFormatter)
}

private fun formatSize(bytes: Long): String {
    return when {
        bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
        bytes >= 1024 -> "%.0f KB".format(bytes / 1024.0)
        else -> "$bytes B"
    }
}

private fun needsStoragePermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
}

private fun enqueueDownload(context: Context, url: String, fileName: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(fileName)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)
}