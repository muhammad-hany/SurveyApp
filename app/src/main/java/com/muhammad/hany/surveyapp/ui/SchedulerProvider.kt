package com.muhammad.hany.surveyapp.ui

import io.reactivex.rxjava3.core.Scheduler

interface SchedulerProvider {
    fun mainThread(): Scheduler
    fun ioThread(): Scheduler
    fun computationThread(): Scheduler
}