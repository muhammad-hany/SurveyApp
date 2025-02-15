package com.muhammad.hany.surveyapp.store

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.ui.AnswerSubmitting
import com.muhammad.hany.surveyapp.ui.QuestionLoading
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion
import com.xm.tka.Effects
import com.xm.tka.ReduceContext
import com.xm.tka.Reduced
import com.xm.tka.cancellable
import com.xm.tka.toEffect

fun HomeAction.GetQuestions.reduced(
    context: ReduceContext<HomeState, HomeAction>,
    state: HomeState,
    env: SurveyEnvironment
): Reduced<HomeState, HomeAction> = with(context) {
    // do nothing if questions already loaded
    if (state.questions.isNotEmpty()) return@with state + Effects.none()
    state.copy(
        isLoading = true
    ) + env.repository.getQuestions()
        .subscribeOn(env.schedulerProvider.ioThread())
        .observeOn(env.schedulerProvider.mainThread())
        .map<HomeAction> { HomeAction.QuestionsLoaded(it) }
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
        .subscribeOn(env.schedulerProvider.ioThread())
        .observeOn(env.schedulerProvider.mainThread())
        .map<SurveyAction> { SurveyAction.AnswerSubmitted(it) }
        .toEffect()
        .cancellable(AnswerSubmitting)
}

fun HomeAction.QuestionsLoaded.reduced(
    context: ReduceContext<HomeState, HomeAction>,
    state: HomeState,
): Reduced<HomeState, HomeAction> = with(context) {
    result.fold(
        onSuccess = { data ->
            state.copy(
                questions = data,
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

fun SurveyAction.CreateNewSurvey.reduced(
    context: ReduceContext<SurveyState, SurveyAction>,
    state: SurveyState,
    env: SurveyEnvironment
): Reduced<SurveyState, SurveyAction> = with(context) {
    if (state.surveyQuestions.isNotEmpty()) return@with state + Effects.none()
    state + Effects.just(
        SurveyAction.SurveyLoaded(
            env.repository
                .getInMemoryQuestions()
                .map { SurveyQuestion(it) }
        )
    )
}

fun SurveyAction.SurveyLoaded.reduced(
    context: ReduceContext<SurveyState, SurveyAction>,
    state: SurveyState
): Reduced<SurveyState, SurveyAction> = with(context) {
    state.copy(
        surveyQuestions = surveyQuestions,
        isLoading = false
    ) + Effects.none()
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
