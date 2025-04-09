package com.junior0028.miniproject.ui.theme.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
                val questionText = stringResource(id = questionResId)
                var expanded by remember { mutableStateOf(false) }
                val selected = dropdownAnswers[questionResId] ?: ""

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selected,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = questionText) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
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

            Text(text = stringResource(R.string.radio_question))
            Row {
                RadioButton(
                    selected = radioAnswer == context.getString(R.string.yes),
                    onClick = { radioAnswer = context.getString(R.string.yes) }
                )
                Text(context.getString(R.string.yes), modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = radioAnswer == context.getString(R.string.no),
                    onClick = { radioAnswer = context.getString(R.string.no) }
                )
                Text(context.getString(R.string.no), modifier = Modifier.padding(start = 8.dp))
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
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    text = "${name.value}, $it",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { shareResult(context, name.value, it) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.share))
                }
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

fun shareResult(context: Context, name: String, result: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Hasil Kuis Kesehatan")
        putExtra(Intent.EXTRA_TEXT, "$name, $result")
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)))
}
