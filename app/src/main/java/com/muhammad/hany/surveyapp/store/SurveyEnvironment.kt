package com.muhammad.hany.surveyapp.store

import com.muhammad.hany.surveyapp.data.repository.Repository

data class SurveyEnvironment(
    val repository: Repository,
    val schedulerProvider: SchedulerProvider
)