package uk.me.danielharman.kotlinspringbot.models.rabbitmq

import kotlinx.serialization.Serializable

@Serializable
data class DuplicateMessage(val MessageId: String,
                            val ImgHash: ULong,
                            val ChannelId: String,
                            val AuthorId: String,
                            val PlatformType: String)