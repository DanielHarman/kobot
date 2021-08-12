package uk.me.danielharman.kotlinspringbot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmmryResponse(
        @SerialName("sm_api_message")
        val sm_api_message: String,
        @SerialName("sm_api_character_count")
        val sm_api_character_count: String,
        @SerialName("sm_api_content_reduced")
        val sm_api_content_reduced: String,
        @SerialName("sm_api_title")
        val sm_api_title: String,
        @SerialName("sm_api_content")
        val sm_api_content: String,
        @SerialName("sm_api_limitation")
        val sm_api_limitation: String
)