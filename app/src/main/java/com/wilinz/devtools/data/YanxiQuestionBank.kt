package com.wilinz.devtools.data

import com.squareup.moshi.Json
data class YanxiQuestionBankRequest(
    //      more: true
//      __show_details__: false,
    @Json(name = "more")
    var more: Boolean = true,
    @Json(name = "__show_details__")
    var showDetails: Boolean = false,
    @Json(name = "options")
    var options: String? = null, // ${options}
    @Json(name = "title")
    var title: String, // ${title}
    @Json(name = "token")
    var token: String, // xxx
    @Json(name = "type")
    var type: String? = null // ${type}
)

data class YanxiQuestionBankResponse(
    @Json(name = "code")
    var code: Int, // 1
    @Json(name = "data")
    var `data`: Data,
    @Json(name = "message")
    var message: String // 请求成功
)

data class Data(
    @Json(name = "results")
    var results: List<Result> = listOf(),
    @Json(name = "times")
    var times: Int = 0 // 421
)

data class Result(
    @Json(name = "answer")
    var answer: String, // 辩证法思想
    @Json(name = "question")
    var question: String // 德国古典哲学是马克思主义哲学的直接理论来源。马克思、恩格斯吸取了黑格尔哲学的______
)