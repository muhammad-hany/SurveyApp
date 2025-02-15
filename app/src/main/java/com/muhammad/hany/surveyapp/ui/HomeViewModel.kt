package com.muhammad.hany.surveyapp.ui

import androidx.lifecycle.ViewModel
import com.muhammad.hany.surveyapp.store.SurveyAction
import com.muhammad.hany.surveyapp.store.SurveyEnvironment
import com.muhammad.hany.surveyapp.store.reduced
import com.muhammad.hany.surveyapp.store.SurveyState
import com.xm.tka.Reducer
import com.xm.tka.Store

class HomeViewModel(
    environment: SurveyEnvironment
) : ViewModel() {

    val store = Store(
        initialState = SurveyState(),
        reducer = reducer,
        environment = environment
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

object QuestionLoading
object AnswerSubmitting


