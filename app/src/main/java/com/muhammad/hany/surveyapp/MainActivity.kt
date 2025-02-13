@file:OptIn(ExperimentalFoundationApi::class)

package com.muhammad.hany.surveyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.muhammad.hany.surveyapp.ui.HomeBar
import com.muhammad.hany.surveyapp.ui.SurveyState
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
    val pagerState = rememberPagerState(pageCount = { surveyState.value.questions.size })

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeBar(
                pagerState = pagerState
            )
        }
    ) { innerPadding ->
        QuestionScreen(
            state = surveyState.value,
            pagerState = pagerState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){ answer, page ->}
    }
}

@Composable
fun QuestionScreen(
    state: SurveyState,
    pagerState: PagerState,
    modifier: Modifier,
    onAnswer: (String, Int) -> Unit
) {

    HorizontalPager(
        state = pagerState
    ) { page ->

        Box(modifier) {


            val question = state.questions.getOrNull(page) ?: return@HorizontalPager
            var answerState by remember { mutableStateOf("") }
            Column {
                Text(
                    text = question.question ?: "",
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = answerState,
                    onValueChange = {
                        answerState = it
                        onAnswer(it, page)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


    }
}

