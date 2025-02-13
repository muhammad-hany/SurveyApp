package com.muhammad.hany.surveyapp.data.repository

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.ApiState
import com.muhammad.hany.surveyapp.data.model.Question
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun getQuestions(): Flow<ApiState<List<Question>>>

    suspend fun submitAnswer(answer: Answer): Flow<ApiState<Unit>>
}