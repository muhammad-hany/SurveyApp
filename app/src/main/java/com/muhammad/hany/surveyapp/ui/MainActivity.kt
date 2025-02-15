package com.muhammad.hany.surveyapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.muhammad.hany.surveyapp.ui.navigation.Home
import com.muhammad.hany.surveyapp.ui.navigation.Survey
import com.muhammad.hany.surveyapp.ui.navigation.SurveyAppNavigation
import com.muhammad.hany.surveyapp.ui.screens.HomeScreen
import com.muhammad.hany.surveyapp.ui.screens.SurveyScreen
import com.muhammad.hany.surveyapp.ui.theme.SurveyAppTheme
import com.xm.tka.ui.ViewStore.Companion.view
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
    val navController = rememberNavController()

    CompositionLocalProvider(
        LOCAL_NAVIGATOR provides navController
    ) {
        SurveyAppNavigation(navController = navController) {
            composable(Home) { HomeScreen(viewModel.store.homeScope().view()) }
            composable(Survey) { SurveyScreen(viewModel.store.surveyScope().view()) }
        }
    }

}




