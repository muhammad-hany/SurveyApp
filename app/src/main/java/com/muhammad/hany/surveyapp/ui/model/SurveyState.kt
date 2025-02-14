package com.muhammad.hany.surveyapp.ui.model

data class SurveyState(
    val surveyQuestions: List<SurveyQuestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val answeredQuestionsCount = surveyQuestions.count { it.answer != null && it.successfullyAnswered }
}