package com.application.bibileapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BibleApiResponse(
    val reference: String,
    val text: String,
    @SerialName("translation_id")
    val translationID: String,
    @SerialName("translation_name")
    val translationame: String,
    val translation_note: String,
    val verses: List<Verse>
)