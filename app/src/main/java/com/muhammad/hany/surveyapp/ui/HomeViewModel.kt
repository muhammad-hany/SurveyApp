package com.muhammad.hany.surveyapp.ui

import androidx.lifecycle.ViewModel
import com.muhammad.hany.surveyapp.store.GlobalAction
import com.muhammad.hany.surveyapp.store.GlobalState
import com.muhammad.hany.surveyapp.store.HomeAction
import com.muhammad.hany.surveyapp.store.HomeState
import com.muhammad.hany.surveyapp.store.SurveyAction
import com.muhammad.hany.surveyapp.store.SurveyEnvironment
import com.muhammad.hany.surveyapp.store.SurveyState
import com.muhammad.hany.surveyapp.store.reduced
import com.xm.tka.ActionPrism
import com.xm.tka.Reducer
import com.xm.tka.StateLens
import com.xm.tka.Store

class HomeViewModel(
    environment: SurveyEnvironment
) : ViewModel() {
    val store = Store(
        initialState = GlobalState(),
        reducer = globalReducer,
        environment = environment
    )
}

fun Store<GlobalState, GlobalAction>.homeScope(): Store<HomeState, HomeAction> =
    this.scope(
        toLocalState = { it.home },
        fromLocalAction = { GlobalAction.Home(it) }
    )

fun Store<GlobalState, GlobalAction>.surveyScope(): Store<SurveyState, SurveyAction> =
    this.scope(
        toLocalState = { it.survey },
        fromLocalAction = { GlobalAction.Survey(it) }
    )

private val homeReducer = Reducer<HomeState, HomeAction, SurveyEnvironment> { state, action, env ->
    when (action) {
        is HomeAction.QuestionsLoaded -> action.reduced(this, state)
        is HomeAction.GetQuestions -> action.reduced(this, state, env)
    }
}

private val surveyReducer = Reducer<SurveyState, SurveyAction, SurveyEnvironment> { state, action, env ->
    when (action) {
        is SurveyAction.CreateNewSurvey -> action.reduced(this, state, env)
        is SurveyAction.SurveyLoaded -> action.reduced(this, state)
        is SurveyAction.ResetQuestion -> action.reduced(this, state)
        is SurveyAction.SubmitAnswer -> action.reduced(this, state, env)
        is SurveyAction.AnswerSubmitted -> action.reduced(this, state)
    }
}

val globalReducer: Reducer<GlobalState, GlobalAction, SurveyEnvironment> = Reducer.combine(
    homeReducer.pullback(
        toLocalState = StateLens(
            set = { state, update -> state.copy(home = update) },
            get = { it.home }
        ),
        toLocalAction = ActionPrism(
            get = {
                (it as? GlobalAction.Home)?.action
                  },
            reverseGet = { GlobalAction.Home(it) }
        ),
        toLocalEnvironment = { it }
    ),
    surveyReducer.pullback(
        toLocalState = StateLens(
            set = { state, update -> state.copy(survey = update) },
            get = { it.survey }
        ),
        toLocalAction = ActionPrism(
            get = { (it as? GlobalAction.Survey)?.action },
            reverseGet = { GlobalAction.Survey(it) }
        ),
        toLocalEnvironment = { it }
    )
)


object QuestionLoading
object AnswerSubmitting


