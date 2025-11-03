package com.application.bibileapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Verse(
    @SerialName("book_id")
    val bookId: String, //then any missing field in the JSON will cause an exception at parse time:
    @SerialName("book_name")
    val bookName: String,
    val chapter: Int?,
    val text: String,
    val verse: Int
)