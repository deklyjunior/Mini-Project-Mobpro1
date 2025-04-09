package com.junior0028.miniproject.ui.theme.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.junior0028.miniproject.R
import com.junior0028.miniproject.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val name = rememberSaveable { mutableStateOf("") }

    val questions = listOf(
        R.string.q_breathing,
        R.string.q_dizzy,
        R.string.q_fever,
        R.string.q_cough,
        R.string.q_chest_pain
    )

    val dropdownAnswers = remember { mutableStateMapOf<Int, String>() }
    var radioAnswer by rememberSaveable { mutableStateOf<String?>(null) }
    var resultText by rememberSaveable { mutableStateOf<String?>(null) }
    var showAlert by remember { mutableStateOf(false) }

    val options = listOf(
        context.getString(R.string.yes),
        context.getString(R.string.no)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quiz_title)) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Quiz.route) }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text(stringResource(R.string.your_name)) },
                placeholder = { Text(stringResource(R.string.enter_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            questions.forEach { questionResId ->
                Text(text = stringResource(id = questionResId))

                var expanded by remember { mutableStateOf(false) }
                val selected = dropdownAnswers[questionResId] ?: ""

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(text = if (selected.isEmpty()) stringResource(R.string.select_answer) else selected)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    dropdownAnswers[questionResId] = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Text(text = "Apakah Anda sedang dalam pengobatan?")
            Row {
                RadioButton(
                    selected = radioAnswer == "Ya",
                    onClick = { radioAnswer = "Ya" }
                )
                Text("Ya", modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = radioAnswer == "Tidak",
                    onClick = { radioAnswer = "Tidak" }
                )
                Text("Tidak", modifier = Modifier.padding(start = 8.dp))
            }

            Button(
                onClick = {
                    if (name.value.isBlank() || dropdownAnswers.size < questions.size || dropdownAnswers.values.any { it.isEmpty() } || radioAnswer == null) {
                        showAlert = true
                    } else {
                        val yesCount = dropdownAnswers.values.count { it == context.getString(R.string.yes) }
                        resultText = when {
                            yesCount >= 4 -> context.getString(R.string.result_critical)
                            yesCount >= 2 -> context.getString(R.string.result_moderate)
                            else -> context.getString(R.string.result_normal)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.evaluate))
            }

            resultText?.let {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (showAlert) {
                AlertDialog(
                    onDismissRequest = { showAlert = false },
                    confirmButton = {
                        TextButton(onClick = { showAlert = false }) {
                            Text(stringResource(R.string.ok))
                        }
                    },
                    title = { Text(stringResource(R.string.warning_title)) },
                    text = { Text(stringResource(R.string.warning_incomplete)) }
                )
            }
        }
    }
}
