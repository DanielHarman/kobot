package uk.me.danielharman.kotlinspringbot.services

import uk.me.danielharman.kotlinspringbot.models.rabbitmq.DuplicateMessageResponse
import uk.me.danielharman.kotlinspringbot.models.rabbitmq.ImagePostMessage

interface MessagingPlatformService {

    fun sendImageDuplicationResponse(imagePostMessage: ImagePostMessage, messageResponse: DuplicateMessageResponse)

}