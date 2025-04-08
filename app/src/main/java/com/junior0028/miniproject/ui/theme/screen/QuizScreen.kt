package com.junior0028.miniproject.ui.theme.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.junior0028.miniproject.R

@Composable
fun QuizScreen(navController: NavHostController) {
    val context = LocalContext.current

    val questions = listOf(
        R.string.q_sesak_napas,
        R.string.q_pusing,
        R.string.q_demam,
        R.string.q_batuk,
        R.string.q_nyeri_dada
    )

    val answers = remember { mutableStateMapOf<Int, Boolean?>() }
    var resultText by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleMedium
        )

        questions.forEach { questionResId ->
            QuestionItem(
                question = stringResource(id = questionResId),
                selectedAnswer = answers[questionResId],
                onAnswerSelected = { answer ->
                    answers[questionResId] = answer
                }
            )
        }

        Button(
            onClick = {
                if (answers.values.any { it == null }) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.warning_incomplete),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    resultText = evaluateAnswers(answers.mapValues { it.value == true }, context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
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
    }
}

@Composable
fun QuestionItem(
    question: String,
    selectedAnswer: Boolean?,
    onAnswerSelected: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = question, style = MaterialTheme.typography.bodyMedium)
        Row {
            Row(
                Modifier
                    .selectable(
                        selected = selectedAnswer == true,
                        onClick = { onAnswerSelected(true) },
                        role = Role.RadioButton
                    )
                    .padding(8.dp)
            ) {
                RadioButton(selected = selectedAnswer == true, onClick = null)
                Text(
                    text = stringResource(R.string.ya),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Row(
                Modifier
                    .selectable(
                        selected = selectedAnswer == false,
                        onClick = { onAnswerSelected(false) },
                        role = Role.RadioButton
                    )
                    .padding(8.dp)
            ) {
                RadioButton(selected = selectedAnswer == false, onClick = null)
                Text(
                    text = stringResource(R.string.tidak),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

fun evaluateAnswers(answers: Map<Int, Boolean>, context: android.content.Context): String {
    val yesCount = answers.count { it.value }

    return when {
        yesCount >= 4 -> context.getString(R.string.result_critical)
        yesCount >= 2 -> context.getString(R.string.result_moderate)
        else -> context.getString(R.string.result_normal)
    }
}