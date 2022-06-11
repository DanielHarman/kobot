package uk.me.danielharman.kotlinspringbot.models.rabbitmq

import kotlinx.serialization.Serializable

@Serializable
data class ImagePostMessage(val MessageId: String,
                            val ImageBytes: String,
                            val ChannelId: String,
                            val AuthorId: String,
                            val PlatformType: String)