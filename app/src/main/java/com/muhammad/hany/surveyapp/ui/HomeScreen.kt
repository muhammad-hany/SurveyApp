package com.muhammad.hany.surveyapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammad.hany.surveyapp.HomeViewModel
import com.muhammad.hany.surveyapp.ui.navigation.Survey

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val surveyState by viewModel.surveyState.collectAsStateWithLifecycle()
    val navController = LOCAL_NAVIGATOR.current

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            if (surveyState.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text(
                "Welcome", modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(12.dp)
            )
            Spacer(Modifier.weight(1F))
            Button(
                enabled = surveyState.surveyQuestions.isNotEmpty(),
                onClick = {
                    navController.navigate(Survey)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(12.dp)
            ) {
                Text("Start Survey", fontSize = 20.sp)
            }
            if (!surveyState.error.isNullOrBlank()) {
                Button(onClick = { viewModel.getQuestions() }, enabled = !surveyState.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Retry")
                        Text(text = "Retry")
                    }

                }
                Text(text = surveyState.error!!)
            }

            Spacer(Modifier.weight(1F))
        }
    }
}
