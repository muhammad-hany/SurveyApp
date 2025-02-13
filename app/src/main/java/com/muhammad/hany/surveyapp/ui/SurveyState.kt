package com.muhammad.hany.surveyapp.ui

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question

data class SurveyState (
    val questions: List<Question> = emptyList(),
    val answers: List<Answer> = emptyList(),
)