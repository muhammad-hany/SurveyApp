package com.muhammad.hany.surveyapp

import androidx.lifecycle.ViewModel
import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.repository.Repository
import com.muhammad.hany.surveyapp.ui.AnswerFailure
import com.muhammad.hany.surveyapp.ui.SurveyAction
import com.muhammad.hany.surveyapp.ui.SurveyEnvironment
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion
import com.muhammad.hany.surveyapp.ui.model.SurveyState
import com.xm.tka.Effects
import com.xm.tka.ReduceContext
import com.xm.tka.Reduced
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
        is SurveyAction.GetQuestions -> action.reduced(this, state, env)
        is SurveyAction.ResetQuestion -> action.reduced(this, state)
        is SurveyAction.SubmitAnswer -> action.reduced(this, state, env)
        is SurveyAction.QuestionsLoaded -> action.reduced(this, state)
        is SurveyAction.AnswerSubmitted -> action.reduced(this, state)
    }
}

fun SurveyAction.GetQuestions.reduced(
    context: ReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
    env: SurveyEnvironment
): Reduced<SurveyState, SurveyAction> = with(context) {
    state.copy(
        isLoading = true
    ) + env.repository.getQuestions()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map<SurveyAction> { SurveyAction.QuestionsLoaded(it) }
        .toEffect()
        .cancellable(QuestionLoading)
}

fun SurveyAction.ResetQuestion.reduced(
    context: ReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
): Reduced<SurveyState, SurveyAction> = with(context) {
    state.copy(
        surveyQuestions = state.surveyQuestions.map {
            it.copy(
                answer = null,
                hasError = false
            )
        }
    ) + Effects.none()
}

fun SurveyAction.SubmitAnswer.reduced(
    context: ReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
    env: SurveyEnvironment
): Reduced<SurveyState, SurveyAction> = with(context) {
    val answer = Answer(id = id, answerText = answerText)
    state.copy(
        isLoading = true
    ) + env.repository.submitAnswer(answer)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map<SurveyAction> { SurveyAction.AnswerSubmitted(it) }
        .toEffect()
        .cancellable(AnswerSubmitting)
}

fun SurveyAction.QuestionsLoaded.reduced(
    context: ReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
): Reduced<SurveyState, SurveyAction> = with(context) {
    result.fold(
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
}

fun SurveyAction.AnswerSubmitted.reduced(
    context: ReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
): Reduced<SurveyState, SurveyAction> = with(context) {
    val surveyQuestionIndex =
        state.surveyQuestions.indexOfFirst { it.question.id == result.answer.id }
    if (surveyQuestionIndex == -1) return@with state + Effects.none()
    val hasError = result is AnswerFailure
    state.copy(
        surveyQuestions = state.surveyQuestions.updatedWith(
            answer = result.answer,
            hasError = hasError
        ),
        isLoading = false
    ) + Effects.none()
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


