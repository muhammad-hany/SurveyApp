package com.muhammad.hany.surveyapp

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import com.muhammad.hany.surveyapp.data.repository.Repository
import com.muhammad.hany.surveyapp.store.AnswerFailure
import com.muhammad.hany.surveyapp.store.AnswerSuccess
import com.muhammad.hany.surveyapp.store.GlobalAction
import com.muhammad.hany.surveyapp.store.GlobalState
import com.muhammad.hany.surveyapp.store.HomeAction
import com.muhammad.hany.surveyapp.store.HomeState
import com.muhammad.hany.surveyapp.store.SchedulerProvider
import com.muhammad.hany.surveyapp.store.SurveyAction
import com.muhammad.hany.surveyapp.store.SurveyEnvironment
import com.muhammad.hany.surveyapp.store.SurveyState
import com.muhammad.hany.surveyapp.ui.globalReducer
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion
import com.xm.tka.test.TestStore
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class SurveyStoreTest {

    private val repository: Repository = mockk()
    private val schedulerProvider: SchedulerProvider = mockk()
    private val state = GlobalState()
    private val environment = SurveyEnvironment(repository, schedulerProvider)

    private val testScheduler = TestScheduler()

    @Before
    fun setup() {
        every { schedulerProvider.mainThread() } returns testScheduler
        every { schedulerProvider.ioThread() } returns testScheduler
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun testGettingQuestions() {
        val question = Question(id = 1, question = "test")
        every { repository.getQuestions() } returns Single.just(Result.success(listOf(question)))
        TestStore(state, globalReducer, environment).assert {
            send(GlobalAction.Home(HomeAction.GetQuestions)) {
                it.copy(home = it.home.copy(isLoading = true))
            }
            testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)
            val result = Result.success(listOf(question))
            receive(GlobalAction.Home(HomeAction.QuestionsLoaded(result))) {
                it.copy(
                    home = it.home.copy(
                        isLoading = false,
                        questions = listOf(question)
                    )
                )
            }
        }
    }

    @Test
    fun `test getting questions while questions already in state`() {
        val question = Question(id = 1, question = "test")
        val questions = listOf(question)
        val state = GlobalState(home = HomeState(questions = questions))
        TestStore(state, globalReducer, environment).assert {
            // state shouldn't change
            send(GlobalAction.Home(HomeAction.GetQuestions))
        }
    }

    @Test
    fun `test getting questions while error happened`() {
        val exception = Exception()
        every { repository.getQuestions() } returns Single.just(Result.failure(exception))
        TestStore(state, globalReducer, environment).assert {
            send(GlobalAction.Home(HomeAction.GetQuestions)) {
                it.copy(
                    home = it.home.copy(
                        isLoading = true
                    )
                )
            }
            testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)
            receive(GlobalAction.Home(HomeAction.QuestionsLoaded(Result.failure(exception)))) {
                it.copy(
                    home = it.home.copy(
                        isLoading = false,
                        error = "issue happened while fetching survey"
                    )
                )
            }
        }
    }

    @Test
    fun `test creating new survey`() {
        val question = Question(id = 1, question = "test")
        val questions = listOf(question)
        val state = GlobalState(home = HomeState(questions = questions))
        every { repository.getInMemoryQuestions() } returns questions
        val surveyQuestions = listOf(SurveyQuestion(question))

        TestStore(state, globalReducer, environment).assert {
            send(GlobalAction.Survey(SurveyAction.CreateNewSurvey))
            receive(GlobalAction.Survey(SurveyAction.SurveyLoaded(surveyQuestions))) {
                it.copy(
                    survey = it.survey.copy(
                        surveyQuestions = surveyQuestions
                    )
                )
            }
        }
    }

    @Test
    fun `test submitting answer`() {
        val answer = Answer(id = 1, answerText = "test")
        val successAnswer = AnswerSuccess(answer)
        val question = Question(id = 1, question = "test")
        val surveyQuestion = SurveyQuestion(question)
        val questions = listOf(surveyQuestion)
        val surveyState = SurveyState(surveyQuestions = questions)
        val state = GlobalState(survey = surveyState)
        every { repository.submitAnswer(answer) } returns Single.just(successAnswer)
        TestStore(state, globalReducer, environment).assert {
            send(GlobalAction.Survey(SurveyAction.SubmitAnswer(answer.answerText, answer.id))) {
                it.copy(
                    survey = it.survey.copy(
                        isLoading = true
                    )
                )
            }
            testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)
            receive(GlobalAction.Survey(SurveyAction.AnswerSubmitted(successAnswer))) {
                it.copy(
                    survey = it.survey.copy(
                        isLoading = false,
                        surveyQuestions = listOf(surveyQuestion.copy(answer = answer, hasError = false))
                    )
                )
            }
        }
    }

    @Test
    fun `test submitting answer while error happened`() {
        val answer = Answer(id = 1, answerText = "test")
        val exception = Exception()
        val failureAnswer = AnswerFailure(exception, answer)

        val question = Question(id = 1, question = "test")
        val surveyQuestion = SurveyQuestion(question)
        val questions = listOf(surveyQuestion)
        val surveyState = SurveyState(surveyQuestions = questions)
        val state = GlobalState(survey = surveyState)
        every { repository.submitAnswer(answer) } returns Single.just(failureAnswer)
        TestStore(state, globalReducer, environment).assert {
            send(GlobalAction.Survey(SurveyAction.SubmitAnswer(answer.answerText, answer.id))) {
                it.copy(
                    survey = it.survey.copy(
                        isLoading = true
                    )
                )
            }
            testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)
            receive(GlobalAction.Survey(SurveyAction.AnswerSubmitted(failureAnswer))) {
                it.copy(
                    survey = it.survey.copy(
                        isLoading = false,
                        surveyQuestions = listOf(surveyQuestion.copy(answer = answer, hasError = true))
                    )
                )
            }
        }
    }

    @Test
    fun testRestQuestions() {
        val answer = Answer(id = 1, answerText = "test")
        val question = Question(id = 1, question = "test")
        val surveyQuestion = SurveyQuestion(question, answer, hasError = true)
        val questions = listOf(surveyQuestion)
        val surveyState = SurveyState(surveyQuestions = questions)
        val state = GlobalState(survey = surveyState)
        TestStore(state, globalReducer, environment).assert {
            send(GlobalAction.Survey(SurveyAction.ResetQuestion)) {
                it.copy(
                    survey = it.survey.copy(
                        surveyQuestions = it.survey.surveyQuestions.map { surveyQuestion ->
                            surveyQuestion.copy(
                                answer = null,
                                hasError = false
                            )
                        }
                    )
                )
            }
        }
    }
}