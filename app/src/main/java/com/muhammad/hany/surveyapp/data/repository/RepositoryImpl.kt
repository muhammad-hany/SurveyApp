package com.muhammad.hany.surveyapp.data.repository

import com.muhammad.hany.surveyapp.data.api.SurveyApi
import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import com.muhammad.hany.surveyapp.store.AnswerFailure
import com.muhammad.hany.surveyapp.store.AnswerSubmission
import com.muhammad.hany.surveyapp.store.AnswerSuccess
import io.reactivex.rxjava3.core.Single

class RepositoryImpl(private val api: SurveyApi) : Repository {

    private val questions = mutableListOf<Question>()

    override fun getQuestions(): Single<Result<List<Question>>> {
        if (questions.isNotEmpty()) return Single.just(Result.success(questions))
        return api.getQuestions()
            .map {
                questions.clear()
                questions.addAll(it)
                Result.success(it)
            }
            .onErrorReturn { Result.failure(it) }
    }

    override fun getInMemoryQuestions(): List<Question> = questions

    override fun submitAnswer(answer: Answer): Single<AnswerSubmission> {
        return api.submitAnswer(answer)
            .map<AnswerSubmission> { AnswerSuccess(answer) }
            .onErrorReturn { AnswerFailure(it, answer) }
    }

}