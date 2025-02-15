package com.muhammad.hany.surveyapp.store

import com.muhammad.hany.surveyapp.data.model.Question

data class HomeState (
    val questions: List<Question> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)