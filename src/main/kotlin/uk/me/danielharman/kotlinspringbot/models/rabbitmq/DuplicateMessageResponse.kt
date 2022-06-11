package uk.me.danielharman.kotlinspringbot.models.rabbitmq

import kotlinx.serialization.Serializable

@Serializable
data class DuplicateMessageResponse(val DuplicateMessage: List<DuplicateMessage>)