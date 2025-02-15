package com.muhammad.hany.surveyapp.store

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion

sealed class SurveyAction {
    data object CreateNewSurvey : SurveyAction()
    data class SurveyLoaded(val surveyQuestions: List<SurveyQuestion>) : SurveyAction()
    data class SubmitAnswer(val answerText: String, val id: Int) : SurveyAction()
    data object ResetQuestion : SurveyAction()
    data class AnswerSubmitted(val result: AnswerSubmission) : SurveyAction()
}

sealed interface AnswerSubmission {
    val answer: Answer
}

data class AnswerSuccess(override val answer: Answer) : AnswerSubmission
data class AnswerFailure(val error: Throwable, override val answer: Answer) : AnswerSubmission
