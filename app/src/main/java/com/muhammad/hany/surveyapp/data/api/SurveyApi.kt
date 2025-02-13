package com.muhammad.hany.surveyapp.data.api

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import retrofit2.http.GET
import retrofit2.http.POST

interface SurveyApi {

    @GET("questions")
    suspend fun getQuestions(): List<Question>

    @POST("question/submit")
    suspend fun submitAnswer(answer: Answer)
}