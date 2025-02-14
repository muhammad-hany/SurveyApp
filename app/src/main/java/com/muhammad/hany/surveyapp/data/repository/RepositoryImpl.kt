package com.muhammad.hany.surveyapp.data.repository

import com.muhammad.hany.surveyapp.data.api.SurveyApi
import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.ApiState
import com.muhammad.hany.surveyapp.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RepositoryImpl(private val api: SurveyApi) : Repository {

    override suspend fun getQuestions(): Flow<ApiState<List<Question>>> = flow {
        emit(ApiState.Loading)
        val response = api.getQuestions()
        emit(ApiState.Success(response))
    }.catch {
        emit(ApiState.Error(it))
    }.flowOn(Dispatchers.IO)

    override suspend fun submitAnswer(answer: Answer): Flow<ApiState<Unit>> = flow {
        emit(ApiState.Loading)
        val response = api.submitAnswer(answer)
        emit(ApiState.Success(response))
    }.catch {
        emit(ApiState.Error(it))
    }.flowOn(Dispatchers.IO)
}