package com.muhammad.hany.surveyapp.ui

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class SchedulerProviderImpl : SchedulerProvider {

    override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()

    override fun ioThread(): Scheduler = Schedulers.io()

    override fun computationThread(): Scheduler = Schedulers.computation()

}