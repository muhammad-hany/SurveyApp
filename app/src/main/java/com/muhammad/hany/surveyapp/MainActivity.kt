@file:OptIn(ExperimentalFoundationApi::class)

package com.muhammad.hany.surveyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.muhammad.hany.surveyapp.ui.HomeBar
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion
import com.muhammad.hany.surveyapp.ui.model.SurveyState
import com.muhammad.hany.surveyapp.ui.theme.SurveyAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SurveyAppTheme {
                SurveyApp(viewModel)
            }
        }
    }
}

@Composable
fun SurveyApp(viewModel: HomeViewModel) {
    val surveyState = viewModel.surveyState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { surveyState.value.surveyQuestions.size })

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeBar(
                pagerState = pagerState
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (surveyState.value.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            QuestionScreen(
                state = surveyState,
                pagerState = pagerState,
                onAnswer = viewModel::submitAnswer,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun QuestionResponse(survey: SurveyQuestion, onRetry: (String, Int) -> Unit) {
    val errorState = survey.hasError
    val successState = survey.successfullyAnswered
    if (!errorState && !successState) return
    val color = if (errorState) Color.Red else Color.Green
    val statusText = if (errorState) "Error" else "Success"
    val canRetry = errorState && !successState
    Box(
        Modifier
            .fillMaxWidth()
            .height(100.dp),

        ) {
        Surface(
            color = color,
            modifier = Modifier.fillMaxSize(),
            contentColor = Color.Black
        ) {
            Row {
                Text(text = statusText, modifier = Modifier.padding(16.dp).align(Alignment.CenterVertically))
                if (canRetry) {
                    Button(onClick = {
                        if (survey.answer?.answerText != null && survey.question.id != null) {
                            onRetry(survey.answer.answerText, survey.question.id)
                        }
                    }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text("retry")
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionScreen(
    state: State<SurveyState>,
    pagerState: PagerState,
    modifier: Modifier,
    onAnswer: (String, Int) -> Unit
) {

    HorizontalPager(
        state = pagerState,
    ) { page ->


        val survey = state.value.surveyQuestions.getOrNull(page) ?: return@HorizontalPager
        val question = survey.question.question ?: return@HorizontalPager
        // TODO handle empty state here

        val answer = survey.answer
        var answerState by remember { mutableStateOf("") }
        Box {
            Column(modifier.padding(16.dp)) {
                QuestionResponse(survey, onRetry = onAnswer)
                Text(
                    text = question,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                TextField(
                    value = answer?.answerText ?: answerState,
                    onValueChange = {
                        answerState = it
                    },
                    label = { Text("Type here for an Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = answer == null && !survey.hasError
                )

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (answerState.isNotBlank()) {
                            onAnswer(answerState, survey.question.id ?: -1)
                        }
                    },
                    enabled = answer == null && !survey.hasError
                ) {
                    Text(text = if (answer == null) "Submit" else "Already Submitted")
                }
            }
        }
    }


}

