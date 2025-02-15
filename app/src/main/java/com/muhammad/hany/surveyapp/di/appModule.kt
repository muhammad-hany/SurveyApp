package com.muhammad.hany.surveyapp.di

import com.muhammad.hany.surveyapp.ui.HomeViewModel
import com.muhammad.hany.surveyapp.data.api.SurveyApi
import com.muhammad.hany.surveyapp.data.client.RetrofitClient
import com.muhammad.hany.surveyapp.data.repository.Repository
import com.muhammad.hany.surveyapp.data.repository.RepositoryImpl
import com.muhammad.hany.surveyapp.store.SchedulerProvider
import com.muhammad.hany.surveyapp.store.SchedulerProviderImpl
import com.muhammad.hany.surveyapp.store.SurveyEnvironment
import com.squareup.moshi.Moshi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    single {
        RetrofitClient()
    }

    single<Moshi> {
        val client: RetrofitClient = get()
        client.buildMoshiInstance()
    }

    single<Retrofit> {
        val client: RetrofitClient = get()
        val moshi: Moshi = get()
        client.buildRetrofitInstance(moshi)
    }

    single<SurveyApi> {
        val retrofit: Retrofit = get()
        retrofit.create(SurveyApi::class.java)
    }

    single<Repository> {
        RepositoryImpl(api = get())
    }

    single<SchedulerProvider> {
        SchedulerProviderImpl()
    }

    single {
        SurveyEnvironment(
            repository = get(),
            schedulerProvider = get()
        )
    }

    viewModel {
        HomeViewModel(environment = get())
    }

}