package com.muhammad.hany.surveyapp.store

sealed class GlobalAction {
    data class Survey (val action: SurveyAction): GlobalAction()
    data class Home (val action: HomeAction): GlobalAction()
}
