package uk.me.danielharman.kotlinspringbot.command.admin

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.command.interfaces.IAdminCommand

@Component
class AdminPingCommand : IAdminCommand {
    override fun execute(event: GuildMessageReceivedEvent) {
        return event.channel.sendMessage("pong ${event.author.asMention}").queue()
    }

    override fun matchCommandString(str: String): Boolean = str.toLowerCase() == "ping"
    override fun getCommandString(): String {
        return "ping"
    }

}