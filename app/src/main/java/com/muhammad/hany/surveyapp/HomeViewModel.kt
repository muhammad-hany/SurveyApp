package com.muhammad.hany.surveyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammad.hany.surveyapp.data.model.ApiState
import com.muhammad.hany.surveyapp.data.repository.Repository
import com.muhammad.hany.surveyapp.ui.SurveyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _surveyState = MutableStateFlow(SurveyState())
    val surveyState = _surveyState.asStateFlow()
    val currentState get() = _surveyState.value


    init {
        viewModelScope.launch {
            repository.getQuestions().collectLatest {
                when(it) {
                    is ApiState.Error -> {}
                    is ApiState.Loading ->{}
                    is ApiState.Success -> {
                        _surveyState.emit(currentState.copy(questions = it.data))
                    }
                }

            }
        }
    }

}