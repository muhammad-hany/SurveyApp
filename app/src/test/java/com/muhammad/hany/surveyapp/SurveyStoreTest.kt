package com.muhammad.hany.surveyapp

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import com.muhammad.hany.surveyapp.data.repository.Repository
import com.muhammad.hany.surveyapp.store.AnswerFailure
import com.muhammad.hany.surveyapp.store.AnswerSuccess
import com.muhammad.hany.surveyapp.store.SchedulerProvider
import com.muhammad.hany.surveyapp.store.SurveyAction
import com.muhammad.hany.surveyapp.store.SurveyEnvironment
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion
import com.muhammad.hany.surveyapp.store.SurveyState
import com.muhammad.hany.surveyapp.ui.reducer
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
    private val state = SurveyState()
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
        TestStore(state, reducer, environment).assert {
            send(SurveyAction.GetQuestions) {
                it.copy(isLoading = true)
            }
            testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)
            val result = Result.success(listOf(question))
            receive(SurveyAction.QuestionsLoaded(result)) {
                it.copy(isLoading = false, surveyQuestions = listOf(SurveyQuestion(question)))
            }
        }
    }

    @Test
    fun `test getting questions while error happened`() {
        val exception = Exception()
        every { repository.getQuestions() } returns Single.just(Result.failure(exception))
        TestStore(state, reducer, environment).assert {
            send(SurveyAction.GetQuestions) {
                it.copy(isLoading = true)
            }
            testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)
            receive(SurveyAction.QuestionsLoaded(Result.failure(exception))) {
                it.copy(isLoading = false, error = "issue happened while fetching survey")
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
        val state = SurveyState(surveyQuestions = questions)
        every { repository.submitAnswer(answer) } returns Single.just(successAnswer)
        TestStore(state, reducer, environment).assert {
            send(SurveyAction.SubmitAnswer(answer.answerText, answer.id)) {
                it.copy(isLoading = true)
            }
            testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)
            receive(SurveyAction.AnswerSubmitted(successAnswer)) {
                it.copy(
                    isLoading = false,
                    surveyQuestions = listOf(surveyQuestion.copy(answer = answer, hasError = false))
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
        val state = SurveyState(surveyQuestions = questions)
        every { repository.submitAnswer(answer) } returns Single.just(failureAnswer)
        TestStore(state, reducer, environment).assert {
            send(SurveyAction.SubmitAnswer(answer.answerText, answer.id)) {
                it.copy(isLoading = true)
            }
            testScheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS)
            receive(SurveyAction.AnswerSubmitted(failureAnswer)) {
                it.copy(
                    isLoading = false,
                    surveyQuestions = listOf(surveyQuestion.copy(answer = answer, hasError = true))
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
        val state = SurveyState(surveyQuestions = questions)

        TestStore(state, reducer, environment).assert {
            send(SurveyAction.ResetQuestion) {
                it.copy(
                    surveyQuestions = it.surveyQuestions.map { surveyQuestion ->
                        surveyQuestion.copy(
                            answer = null,
                            hasError = false
                        )
                    }
                )
            }
        }
    }
}