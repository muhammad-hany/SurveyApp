package com.muhammad.hany.surveyapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SurveyAppNavigation(
    navController: NavHostController,
    navigationBuilder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() },
        builder = navigationBuilder
    )
}

@ExperimentalAnimationApi
private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultEnterTransition(): EnterTransition {
    return slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start)
}

@ExperimentalAnimationApi
private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultExitTransition(): ExitTransition {
    return slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start)
}


@ExperimentalAnimationApi
private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultPopEnterTransition(): EnterTransition {
    return fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End)
}

@ExperimentalAnimationApi
private fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultPopExitTransition(): ExitTransition {
    return slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End)
}