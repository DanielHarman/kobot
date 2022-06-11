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
import uk.me.danielharman.kotlinspringbot.models.rabbitmq.DuplicateMessage
import uk.me.danielharman.kotlinspringbot.models.rabbitmq.DuplicateMessageResponse
import uk.me.danielharman.kotlinspringbot.models.rabbitmq.ImagePostMessage
import uk.me.danielharman.kotlinspringbot.objects.DiscordObject
import java.util.function.Consumer
import kotlin.text.toByteArray

@Service
class RabbitMqSender(@Autowired private val rabbitTemplate : AmqpTemplate,
                     @Autowired private val queue : Queue) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun send(imagePostMessage: ImagePostMessage){

        //TODO: Extract the discord functionality to separate service that handles interacting with the library
        logger.info("Sending Message to the Queue (${queue.name}): $imagePostMessage")
        var messageJson = Json.encodeToString(imagePostMessage)
        val message = MessageBuilder.withBody(messageJson.toByteArray()).build()
        val response = rabbitTemplate.sendAndReceive(queue.name, message) ?: return
        var messageResponse = Json.decodeFromString<DuplicateMessageResponse>(String(response.body))

        var listOfJumpUrlsMessage = ""
        val channel = DiscordObject.jda.getTextChannelById(imagePostMessage.ChannelId)
        if (response != null) {
            messageResponse.DuplicateMessage.forEach(Consumer { (MessageId, _, _, _): DuplicateMessage ->
                listOfJumpUrlsMessage = listOfJumpUrlsMessage + channel!!.retrieveMessageById(MessageId).complete().jumpUrl + "\n"
            })

            channel!!.sendMessage("You have posted an image that has already been posted ${messageResponse.DuplicateMessage.count()} times, this might be a repost. Here are the message links: $listOfJumpUrlsMessage").queue()
        }

    }
}