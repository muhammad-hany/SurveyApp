package com.muhammad.hany.surveyapp

import androidx.lifecycle.ViewModel
import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.repository.Repository
import com.muhammad.hany.surveyapp.ui.AnswerSubmission
import com.muhammad.hany.surveyapp.ui.SurveyAction
import com.muhammad.hany.surveyapp.ui.SurveyEnvironment
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion
import com.muhammad.hany.surveyapp.ui.model.SurveyState
import com.xm.tka.Effects
import com.xm.tka.Reducer
import com.xm.tka.Store
import com.xm.tka.cancellable
import com.xm.tka.toEffect
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel(
    repository: Repository
) : ViewModel() {

    val store = Store(
        initialState = SurveyState(),
        reducer = reducer,
        environment = SurveyEnvironment(repository)
    )

}

val reducer = Reducer<SurveyState, SurveyAction, SurveyEnvironment> { state, action, env ->
    when (action) {
        SurveyAction.GetQuestions -> state.copy(
            isLoading = true
        ) + env.repository.getQuestions()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map<SurveyAction> { SurveyAction.QuestionsLoaded(it) }
            .toEffect()
            .cancellable(QuestionLoading)

        SurveyAction.ResetQuestion -> state.copy(
            surveyQuestions = state.surveyQuestions.map {
                it.copy(
                    answer = null,
                    hasError = false
                )
            }
        ) + Effects.none()

        is SurveyAction.SubmitAnswer -> {
            val answer = Answer(id = action.id, answerText = action.answerText)
            state.copy(
                isLoading = true
            ) + env.repository.submitAnswer(answer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map<SurveyAction> { SurveyAction.AnswerSubmitted(it) }
                .toEffect()
                .cancellable(AnswerSubmitting)
        }

        is SurveyAction.QuestionsLoaded -> action.result.fold(
            onSuccess = { data ->
                state.copy(
                    surveyQuestions = data.map { SurveyQuestion(it) },
                    isLoading = false,
                    error = null
                ) + Effects.none()
            },
            onFailure = { _ ->
                state.copy(
                    error = "issue happened while fetching survey",
                    isLoading = false
                ) + Effects.none()
            }
        )

        is SurveyAction.AnswerSubmitted -> {
            val surveyQuestionIndex =
                state.surveyQuestions.indexOfFirst { it.question.id == action.result.answer.id }
            if (surveyQuestionIndex == -1) return@Reducer state + Effects.none()
            val hasError = action.result is AnswerSubmission.Failure
            state.copy(
                surveyQuestions = state.surveyQuestions.updatedWith(
                    answer = action.result.answer,
                    hasError = hasError
                ),
                isLoading = false
            ) + Effects.none()
        }
    }
}

fun List<SurveyQuestion>.updatedWith(answer: Answer, hasError: Boolean): List<SurveyQuestion> {
    return toMutableList().apply {
        val surveyQuestionIndex = indexOfFirst { it.question.id == answer.id }
        if (surveyQuestionIndex == -1) return this
        this[surveyQuestionIndex] =
            this[surveyQuestionIndex].copy(answer = answer, hasError = hasError)
    }
}

object QuestionLoading
object AnswerSubmitting


