package uk.me.danielharman.kotlinspringbot.command.dashboard

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.properties.DashboardProperties
import uk.me.danielharman.kotlinspringbot.command.interfaces.IAdminCommand
import java.util.*

@Component
class LoginCommand(private val dashboardProperties: DashboardProperties): IAdminCommand {

    private val commandString = "login"

    override fun execute(event: PrivateMessageReceivedEvent) {
        event.channel.sendMessage("${dashboardProperties.rootAddress}/login?userId=${event.author.id}&token=${UUID.randomUUID()}").queue()
    }

    override fun matchCommandString(str: String): Boolean = str == commandString
    override fun getCommandString(): String = commandString
}