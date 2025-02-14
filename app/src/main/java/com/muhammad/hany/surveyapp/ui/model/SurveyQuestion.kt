package com.muhammad.hany.surveyapp.ui.model

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question

data class SurveyQuestion (
    val question: Question,
    val answer: Answer? = null,
    val hasError: Boolean = false
) {
    val successfullyAnswered: Boolean = answer != null && !hasError
}