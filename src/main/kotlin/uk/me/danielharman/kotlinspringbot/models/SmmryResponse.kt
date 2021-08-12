package uk.me.danielharman.kotlinspringbot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmmryResponse(
        @SerialName("sm_api_message")
        val apiMessage: String,
        @SerialName("sm_api_character_count")
        val characterCount: String,
        @SerialName("sm_api_content_reduced")
        val contentReduced: String,
        @SerialName("sm_api_title")
        val title: String,
        @SerialName("sm_api_content")
        val content: String,
        @SerialName("sm_api_limitation")
        val limitationMessage: String
)