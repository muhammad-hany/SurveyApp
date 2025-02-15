package com.muhammad.hany.surveyapp.data.repository

import com.muhammad.hany.surveyapp.data.model.Answer
import com.muhammad.hany.surveyapp.data.model.Question
import com.muhammad.hany.surveyapp.store.AnswerSubmission
import io.reactivex.rxjava3.core.Single

interface Repository {
    fun getQuestions(): Single<Result<List<Question>>>
    fun submitAnswer(answer: Answer): Single<AnswerSubmission>
}