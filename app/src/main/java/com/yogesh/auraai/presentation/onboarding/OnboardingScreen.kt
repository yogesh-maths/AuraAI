package com.yogesh.auraai.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yogesh.auraai.core.di.AppContainer

@Composable
fun OnboardingScreen(
    appContainer: AppContainer,
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = appContainer.viewModelFactory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            when (uiState.currentStep) {

                1 -> {

                    Text(
                        text = "Welcome to AuraAI",
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Text(
                        text = "Personalized AI Assistant",
                        modifier = Modifier.padding(top = 24.dp)
                    )

                    Button(
                        onClick = { viewModel.nextStep() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Text("Next")
                    }
                }

                2 -> {

                    Text(
                        text = "Create Your Profile",
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    TextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Name") }
                    )

                    TextField(
                        value = uiState.age,
                        onValueChange = viewModel::updateAge,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Age") }
                    )

                    TextField(
                        value = uiState.phone,
                        onValueChange = viewModel::updatePhone,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Phone Number") }
                    )

                    TextField(
                        value = uiState.otp,
                        onValueChange = viewModel::updateOtp,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("OTP") }
                    )

                    uiState.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = { viewModel.nextStep() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }
                }

                3 -> {

                    Text(
                        text = "Select 3 Traits",
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    listOf(
                        "Creative",
                        "Logical",
                        "Friendly",
                        "Leader",
                        "Calm",
                        "Funny"
                    ).forEach { trait ->

                        Button(
                            onClick = {
                                viewModel.toggleTrait(trait)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(trait)
                        }
                    }

                    Text(
                        text = "Selected: ${uiState.selectedTraits.size}/3"
                    )

                    uiState.error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            if (uiState.selectedTraits.size == 3) {
                                onComplete()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Finish")
                    }

                    Button(
                        onClick = {
                            viewModel.previousStep()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back")
                    }

                }
            }
        }
    }
}
