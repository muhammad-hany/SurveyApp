@file:OptIn(ExperimentalFoundationApi::class)

package com.muhammad.hany.surveyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.muhammad.hany.surveyapp.ui.SurveyScreen
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
    SurveyScreen(viewModel)
}




