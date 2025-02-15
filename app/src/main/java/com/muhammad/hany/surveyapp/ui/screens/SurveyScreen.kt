package com.muhammad.hany.surveyapp.ui.screens

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.muhammad.hany.surveyapp.store.SurveyAction
import com.muhammad.hany.surveyapp.store.SurveyState
import com.muhammad.hany.surveyapp.ui.LOCAL_NAVIGATOR
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion
import com.xm.tka.ui.ViewStore


@Composable
fun SurveyScreen(
    viewStore: ViewStore<SurveyState, SurveyAction>
) {
    val surveyState by viewStore.states.subscribeAsState(viewStore.currentState)
    val pagerState = rememberPagerState(pageCount = { surveyState.surveyQuestions.size })
    val navController = LOCAL_NAVIGATOR.current

    LaunchedEffect(Unit) {
        viewStore.send(SurveyAction.CreateNewSurvey)
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeBar(
                pagerState = pagerState,
                numberOfAnsweredQuestions = surveyState.answeredQuestionsCount,
                onBackPressed = {
                    navController.navigateUp()
                    viewStore.send(SurveyAction.ResetQuestion)
                })
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (surveyState.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            Question(
                state = surveyState,
                pagerState = pagerState,
                onAnswer = { answer, id -> viewStore.send(SurveyAction.SubmitAnswer(answer, id)) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun Question(
    state: SurveyState,
    pagerState: PagerState,
    modifier: Modifier,
    onAnswer: (String, Int) -> Unit
) {

    HorizontalPager(
        state = pagerState,
    ) { page ->


        val survey = state.surveyQuestions.getOrNull(page) ?: return@HorizontalPager
        val question = survey.question.question ?: return@HorizontalPager

        val answer = survey.answer
        var answerState by remember { mutableStateOf("") }
        var userStartedInput by remember { mutableStateOf(false) }
        val answerTextFieldError by remember { derivedStateOf { answerState.isBlank() && userStartedInput } }

        Box {
            Column(modifier.padding(16.dp)) {
                QuestionResponse(survey, onRetry = onAnswer)
                Text(
                    text = question,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = answer?.answerText ?: answerState,
                    onValueChange = {
                        answerState = it
                        if (!userStartedInput) userStartedInput = true
                    },
                    label = { Text("Type here for an Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = answer == null && !survey.hasError,
                    isError = answerTextFieldError,
                    supportingText = {
                        if (answerTextFieldError) Text("You must enter an answer")
                    }
                )

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (answerState.isNotBlank()) {
                            onAnswer(answerState, survey.question.id ?: -1)
                        }
                        if (!userStartedInput) userStartedInput = true
                    },
                    enabled = answer == null && !survey.hasError && !answerTextFieldError
                ) {
                    Text(text = if (answer == null) "Submit" else "Already Submitted")
                }
            }
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
                Text(
                    text = statusText,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically)
                )
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