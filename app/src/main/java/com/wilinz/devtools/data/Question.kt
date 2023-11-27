package com.wilinz.devtools.data
import com.squareup.moshi.Json

data class Question(
    @Json(name = "answers")
    var answers: List<String> = listOf(),
    @Json(name = "answers_text")
    var answersText: String = "",
    @Json(name = "options")
    var options: List<Option> = listOf(),
    @Json(name = "question")
    var question: String = "", // 塞利格曼是( )国心理学家，主要从事习得性无助、抑郁、乐观主义、悲观主义等方面的研究。
    @Json(name = "question_details")
    var questionDetails: String = "",
    @Json(name = "question_type")
    var questionType: String = ""
)

data class Option(
    @Json(name = "content")
    var content: String? = null, // 美国
    @Json(name = "option")
    var option: String = "" // A
)

