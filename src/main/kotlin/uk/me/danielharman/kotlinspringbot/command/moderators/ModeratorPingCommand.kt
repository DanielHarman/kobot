package uk.me.danielharman.kotlinspringbot.command.moderators

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.command.interfaces.IModeratorCommand

@Component
class ModeratorPingCommand : IModeratorCommand {
    override fun execute(event: MessageReceivedEvent) {
        return event.channel.sendMessage("pong ${event.author.asMention}").queue()
    }

    override fun matchCommandString(str: String): Boolean = str.toLowerCase() == "ping"
    override fun getCommandString(): String {
        return "ping"
    }

}