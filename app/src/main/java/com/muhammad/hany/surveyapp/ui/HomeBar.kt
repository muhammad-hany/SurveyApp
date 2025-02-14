package com.muhammad.hany.surveyapp.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muhammad.hany.surveyapp.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBar(
    pagerState: PagerState,
    numberOfAnsweredQuestions: Int
) {
    val navController = LOCAL_NAVIGATOR.current
    val coroutineScope = rememberCoroutineScope()
    TopAppBar(
        title = {
            Text(
                "Question $numberOfAnsweredQuestions/${pagerState.pageCount}",
                maxLines = 1,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.menu),
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        },
        actions = {
            Row {
                val nextEnabled = pagerState.currentPage < pagerState.pageCount - 1
                val previousEnabled = pagerState.currentPage > 0
                TextButton(
                    enabled = previousEnabled,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }

                    }
                ) {
                    Text(text = "Previous", color = if (previousEnabled) Color.White else Color.Gray)
                }
                TextButton(
                    enabled = nextEnabled,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }) {
                    Text(text = "Next", color = if (nextEnabled) Color.White else Color.Gray)
                }
            }
        }
    )
}