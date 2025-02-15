package com.muhammad.hany.surveyapp.data.repository

import com.muhammad.hany.surveyapp.data.api.SurveyApi
import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import com.muhammad.hany.surveyapp.ui.AnswerSubmission
import io.reactivex.rxjava3.core.Single

class RepositoryImpl(private val api: SurveyApi) : Repository {

    override fun getQuestions(): Single<Result<List<Question>>> {
        return api.getQuestions()
            .map { Result.success(it) }
            .onErrorReturn { Result.failure(it) }
    }

    override fun submitAnswer(answer: Answer): Single<AnswerSubmission> {
        return api.submitAnswer(answer)
            .map<AnswerSubmission> { AnswerSubmission.Success(answer) }
            .onErrorReturn { AnswerSubmission.Failure(it, answer) }
    }

}