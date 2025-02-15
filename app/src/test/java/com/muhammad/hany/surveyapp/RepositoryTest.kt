package com.muhammad.hany.surveyapp

import com.muhammad.hany.surveyapp.data.api.SurveyApi
import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import com.muhammad.hany.surveyapp.data.repository.RepositoryImpl
import com.muhammad.hany.surveyapp.ui.AnswerFailure
import com.muhammad.hany.surveyapp.ui.AnswerSuccess
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Test

class RepositoryTest {

    private val apiService: SurveyApi = mockk()
    private val repository = RepositoryImpl(apiService)

    private val testScheduler = TestScheduler()

    @Before
    fun setup() {
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun testGetQuestions() {
        val questions = listOf(Question(1, "What is your name?"))
        every { apiService.getQuestions() } returns Single.just(questions)
        val observer = repository.getQuestions().test()
        observer.await()
        observer.assertComplete()
            .assertNoErrors()
            .assertValue { result ->
                result.isSuccess && result.getOrNull() == questions
            }
        verify { apiService.getQuestions() }
    }

    @Test
    fun `test getting questions while error happened`() {
        val exception = Exception()
        every { apiService.getQuestions() } returns Single.error(exception)
        val observer = repository.getQuestions().test()
        observer.await()
        observer.assertComplete().onError(exception)
    }

    @Test
    fun testSubmitAnswer() {
        val answerText = "John Doe"
        val questionId = 1
        val answer = Answer(questionId, answerText)
        val answerSubmission = AnswerSuccess(answer)
        every { apiService.submitAnswer(answer) } returns Single.just(Unit)
        val observer = repository.submitAnswer(answer).test()
        observer.await()
            .assertComplete()
            .assertValue {
                it == answerSubmission
            }
    }

    @Test
    fun testSubmitAnswerOnError() {
        val answerText = "John Doe"
        val questionId = 1
        val answer = Answer(questionId, answerText)
        val exception = Exception()
        every { apiService.submitAnswer(answer) } returns Single.error(exception)
        val observer = repository.submitAnswer(answer).test()
        observer.await()
            .assertComplete()
            .assertValue {
                (it as AnswerFailure).error == exception && it.answer == answer
            }
    }
}