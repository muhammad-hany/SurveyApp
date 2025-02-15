package com.muhammad.hany.surveyapp.store

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question

sealed class SurveyAction {
    data object GetQuestions : SurveyAction()
    data class SubmitAnswer(val answerText: String, val id: Int) : SurveyAction()
    data object ResetQuestion : SurveyAction()
    data class QuestionsLoaded(val result: Result<List<Question>>) : SurveyAction()
    data class AnswerSubmitted(val result: AnswerSubmission): SurveyAction()
}

sealed interface AnswerSubmission {
    val answer: Answer
}

data class AnswerSuccess(override val answer: Answer) : AnswerSubmission
data class AnswerFailure(val error: Throwable, override val answer: Answer) : AnswerSubmission
