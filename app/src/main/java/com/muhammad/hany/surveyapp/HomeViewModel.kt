package com.muhammad.hany.surveyapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.ApiState
import com.muhammad.hany.surveyapp.data.repository.Repository
import com.muhammad.hany.surveyapp.ui.model.SurveyQuestion
import com.muhammad.hany.surveyapp.ui.model.SurveyState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _surveyState = MutableStateFlow(SurveyState())
    val surveyState = _surveyState.asStateFlow()
    private val currentState get() = _surveyState.value


    init {
        getQuestions()
    }

    fun getQuestions() {
        viewModelScope.launch {
            repository.getQuestions().collectLatest { apiState ->
                when (apiState) {
                    is ApiState.Error -> {
                        _surveyState.emit(currentState.copy(isLoading = false, error = "issue happened while fetching survey"))
                    }
                    is ApiState.Loading -> {
                        _surveyState.emit(currentState.copy(isLoading = true))
                    }

                    is ApiState.Success -> {
                        _surveyState.emit(
                            currentState.copy(
                                surveyQuestions = apiState.data.map { SurveyQuestion(it) },
                                isLoading = false,
                                error = null
                            )
                        )
                    }
                }

            }
        }
    }

    fun submitAnswer(answerText: String, id: Int) {
        viewModelScope.launch {
            val answer = Answer(id = id, answerText = answerText)
            val surveyQuestionIndex =
                currentState.surveyQuestions.indexOfFirst { it.question.id == id }
            if (surveyQuestionIndex == -1) return@launch

            repository.submitAnswer(answer).collectLatest { apiState ->
                when (apiState) {
                    is ApiState.Error -> {
                        val error = apiState.throwable.message ?: "Unknown Error"
                        Log.e("HomeViewModel", "submitAnswer: $error")
                        val modifiedList = currentState.surveyQuestions.toMutableList()
                        modifiedList[surveyQuestionIndex] =
                            modifiedList[surveyQuestionIndex].copy(hasError = true, answer = answer)
                        _surveyState.emit(
                            currentState.copy(
                                isLoading = false,
                                surveyQuestions = modifiedList
                            )
                        )
                    }

                    is ApiState.Loading -> {
                        _surveyState.emit(currentState.copy(isLoading = true))
                    }

                    is ApiState.Success -> {
                        withContext(Dispatchers.Main) {
                            val modifiedList = currentState.surveyQuestions.toMutableList()
                            modifiedList[surveyQuestionIndex] =
                                modifiedList[surveyQuestionIndex].copy(
                                    answer = answer,
                                    hasError = false
                                )
                            _surveyState.emit(
                                currentState.copy(
                                    surveyQuestions = modifiedList,
                                    isLoading = false
                                )
                            )


                        }
                    }

                }
            }
        }
    }
}