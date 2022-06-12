package uk.me.danielharman.kotlinspringbot.listeners

import io.ktor.utils.io.core.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.models.rabbitmq.DuplicateMessageResponse
import uk.me.danielharman.kotlinspringbot.models.rabbitmq.ImagePostMessage
import uk.me.danielharman.kotlinspringbot.services.MessagingPlatformService
import kotlin.text.toByteArray

@Service
class RabbitMqSender(@Autowired private val messagingPlatformService: MessagingPlatformService,
                     @Autowired private val rabbitTemplate : AmqpTemplate,
                     @Autowired private val queue : Queue) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun send(imagePostMessage: ImagePostMessage){

        logger.info("Sending Message to the Queue (${queue.name}): $imagePostMessage")
        var messageJson = Json.encodeToString(imagePostMessage)
        val originMessage = MessageBuilder.withBody(messageJson.toByteArray()).build()
        val response = rabbitTemplate.sendAndReceive(queue.name, originMessage) ?: return
        var messageResponse = Json.decodeFromString<DuplicateMessageResponse>(String(response.body))

        messagingPlatformService.sendImageDuplicationResponse(imagePostMessage, messageResponse)
    }
}