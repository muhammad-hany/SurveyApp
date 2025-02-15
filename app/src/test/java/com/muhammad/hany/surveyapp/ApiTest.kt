package com.muhammad.hany.surveyapp

import com.google.common.truth.Truth.assertThat
import com.muhammad.hany.surveyapp.data.api.SurveyApi
import com.muhammad.hany.surveyapp.data.client.RetrofitClient
import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import com.squareup.moshi.Types
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class ApiTest {

    private val mockWebServer =  MockWebServer()
    private lateinit var api: SurveyApi

    private val baseUrl = mockWebServer.url("/").toString()
    private val retrofitClient = RetrofitClient(baseUrl)
    private val moshi = retrofitClient.buildMoshiInstance()
    private val retrofit = retrofitClient.buildRetrofitInstance(moshi)


    @Before
    fun setup() {
        // Create an instance of our ApiService
        api = retrofit.create(SurveyApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetQuestions() {
        val question = Question(1, "What is your name?")
        val questions = listOf(question)

        val listType = Types.newParameterizedType(List::class.java, Question::class.java)
        val adapter = moshi.adapter<List<Question>>(listType)
        val responseString =  adapter.toJson(listOf(question))

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(responseString)

        mockWebServer.enqueue(mockResponse)
        val questionsResponse = api.getQuestions().blockingGet()
        assertThat(questionsResponse).isEqualTo(questions)
        assertThat(questionsResponse.first()).isEqualTo(question)
    }

    @Test
    fun testGetQuestionsOnError() {
        val mockResponse = MockResponse()
            .setResponseCode(400)
        mockWebServer.enqueue(mockResponse)
        val observer = api.getQuestions().test()
        observer.await()
            .assertError {
                it.message?.contains(400.toString()) == true
            }

        // Check that the request was actually made
        val request = mockWebServer.takeRequest()
        assert(request.path == "/questions")

    }

    @Test
    fun testSubmitAnswer() {
        val answerText = "John Doe"
        val questionId = 1
        val answer = Answer(questionId, answerText)
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        assertThat(api.submitAnswer(answer).blockingGet()).isEqualTo(Unit)
    }

    @Test
    fun testSubmitAnswerOnError() {
        val answerText = "John Doe"
        val questionId = 1
        val answer = Answer(questionId, answerText)
        val mockResponse = MockResponse()
            .setResponseCode(400)

        mockWebServer.enqueue(mockResponse)
        val observer = api.submitAnswer(answer).test()
        observer.await()
            .assertError {
                it.message?.contains(400.toString()) == true
            }

        // Check that the request was actually made
        val request = mockWebServer.takeRequest()
        assert(request.path == "/question/submit")
    }

}