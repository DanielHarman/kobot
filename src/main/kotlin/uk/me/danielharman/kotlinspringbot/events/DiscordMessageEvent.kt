package uk.me.danielharman.kotlinspringbot.events

import net.dv8tion.jda.api.entities.*
import java.io.InputStream

abstract class DiscordMessageEvent(
    content: String,
    val channel: MessageChannel,
    val author: User,
    val guild: Guild?,
    val attachments: List<Message.Attachment> = listOf(),
    val mentionedUsers: List<User> = listOf()
) : MessageEvent(content, OriginService.Discord) {

    abstract fun reply(embed: MessageEmbed, invokerOnly: Boolean = false)
    abstract fun reply(file: InputStream, filename: String)

}

