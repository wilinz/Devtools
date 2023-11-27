package com.wilinz.devtools.data.repository

import com.wilinz.devtools.data.Network
import com.wilinz.devtools.data.Question
import com.wilinz.devtools.data.YanxiQuestionBankRequest
import com.wilinz.devtools.data.YanxiQuestionBankResponse
import toMap

object YanxiRepository {
    suspend fun get(request: YanxiQuestionBankRequest): YanxiQuestionBankResponse =
        Network.yanxiApi.get(request.toMap())

}