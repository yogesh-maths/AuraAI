package com.yogesh.auraai.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.yogesh.auraai.core.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appContainer: AppContainer,
    onNavigateBack: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = appContainer.viewModelFactory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SettingsUiEvent.NavigateToOnboarding -> onNavigateToOnboarding()
                is SettingsUiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is SettingsUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                SettingsUiEvent.SettingsSaved -> Unit
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear all data?") },
            text = { Text("This will delete all conversations and reset settings.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearAllData()
                    },
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
//            TextField(
//                value = uiState.settings.apiKey,
//                onValueChange = viewModel::updateApiKey,
//                modifier = Modifier.fillMaxWidth(),
//                label = { Text("API Key") },
//                visualTransformation = if (uiState.showApiKey) {
//                    VisualTransformation.None
//                } else {
//                    PasswordVisualTransformation()
//                },
//                trailingIcon = {
//                    IconButton(onClick = viewModel::toggleShowApiKey) {
//                        Icon(
//                            imageVector = if (uiState.showApiKey) {
//                                Icons.Default.VisibilityOff
//                            } else {
//                                Icons.Default.Visibility
//                            },
//                            contentDescription = "Toggle API key visibility",
//                        )
//                    }
//                },
//                singleLine = true,
//            )

//            TextField(
//                value = uiState.settings.modelName,
//                onValueChange = viewModel::updateModel,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 12.dp),
//                label = { Text("Model") },
//                singleLine = true,
//            )
//
//            TextField(
//                value = uiState.settings.systemPrompt,
//                onValueChange = viewModel::updateSystemPrompt,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 12.dp),
//                label = { Text("System prompt") },
//                minLines = 3,
//            )

//            uiState.connectionTestResult?.let { result ->
//                Text(
//                    text = result,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.padding(top = 8.dp),
//                )
//            }

//            Button(
//                onClick = viewModel::saveSettings,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 16.dp),
//                enabled = !uiState.isSaving,
//            ) {
//                Text(if (uiState.isSaving) "Saving…" else "Save settings")
//            }

//            OutlinedButton(
//                onClick = viewModel::testConnection,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                enabled = !uiState.isTestingConnection,
//            ) {
//                Text(if (uiState.isTestingConnection) "Testing…" else "Test connection")
//            }

            OutlinedButton(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text("Clear all data")
            }
            OutlinedButton(
                onClick = {

                    FirebaseAuth.getInstance().signOut()

                    onNavigateToOnboarding()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text("Logout")
            }
        }
    }
}
