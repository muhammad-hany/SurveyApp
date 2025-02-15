package com.muhammad.hany.surveyapp.store

import com.muhammad.hany.surveyapp.data.model.Question

sealed class HomeAction {
    data object GetQuestions : HomeAction()
    data class QuestionsLoaded(val result: Result<List<Question>>) : HomeAction()
}