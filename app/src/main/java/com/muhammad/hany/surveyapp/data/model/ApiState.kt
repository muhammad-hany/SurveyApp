package com.muhammad.hany.surveyapp.data.model

sealed class ApiState<out T> {
    data class Success<out T>(val data: T) : ApiState<T>()
    data object Loading : ApiState<Nothing>()
    data class Error(val throwable: Throwable) : ApiState<Nothing>()
}