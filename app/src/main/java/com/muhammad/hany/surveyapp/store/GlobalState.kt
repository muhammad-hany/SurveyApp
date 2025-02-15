package com.muhammad.hany.surveyapp.store

data class GlobalState (
    val survey: SurveyState = SurveyState(),
    val home: HomeState = HomeState()
)
