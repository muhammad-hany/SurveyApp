package com.muhammad.hany.surveyapp.data.api

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SurveyApi {

    @GET("questions")
    fun getQuestions(): Single<List<Question>>

    @POST("question/submit")
    fun submitAnswer(@Body answer: Answer): Single<Unit>
}