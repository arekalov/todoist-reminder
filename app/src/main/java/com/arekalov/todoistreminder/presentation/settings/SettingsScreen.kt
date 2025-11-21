package com.arekalov.todoistreminder.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Настройки") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Включить/выключить напоминания
            SettingCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Напоминания",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (settings.enabled) "Включены" else "Выключены",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.enabled,
                        onCheckedChange = { viewModel.toggleEnabled(it) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Время начала
            var showStartPicker by remember { mutableStateOf(false) }
            SettingCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Время начала",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${settings.startHour}:00",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        TextButton(onClick = { showStartPicker = !showStartPicker }) {
                            Text("Изменить")
                        }
                    }
                    
                    if (showStartPicker) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = settings.startHour.toFloat(),
                            onValueChange = { viewModel.updateStartHour(it.toInt()) },
                            valueRange = 0f..23f,
                            steps = 22
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Время окончания
            var showEndPicker by remember { mutableStateOf(false) }
            SettingCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Время окончания",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${settings.endHour}:00",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        TextButton(onClick = { showEndPicker = !showEndPicker }) {
                            Text("Изменить")
                        }
                    }
                    
                    if (showEndPicker) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = settings.endHour.toFloat(),
                            onValueChange = { viewModel.updateEndHour(it.toInt()) },
                            valueRange = 0f..23f,
                            steps = 22
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Интервал напоминаний
            SettingCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Интервал напоминаний",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${settings.intervalMinutes} минут",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Минимум 15 минут из-за ограничений Android",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = settings.intervalMinutes.toFloat(),
                        onValueChange = { viewModel.updateInterval(it.toInt()) },
                        valueRange = 15f..180f,
                        steps = 164
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

