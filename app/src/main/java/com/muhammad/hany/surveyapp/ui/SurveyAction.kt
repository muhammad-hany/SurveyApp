package com.muhammad.hany.surveyapp.ui

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question

sealed class SurveyAction {
    data object GetQuestions : SurveyAction()
    data class SubmitAnswer(val answerText: String, val id: Int) : SurveyAction()
    data object ResetQuestion : SurveyAction()
    data class QuestionsLoaded(val result: Result<List<Question>>) : SurveyAction()
    data class AnswerSubmitted(val result: AnswerSubmission): SurveyAction()
}

sealed class AnswerSubmission(val answer: Answer) {
    data class Success(val successAnswer: Answer) : AnswerSubmission(successAnswer)
    data class Failure(val error: Throwable, val failedAnswer: Answer) : AnswerSubmission(failedAnswer)
}