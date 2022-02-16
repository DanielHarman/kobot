package uk.me.danielharman.kotlinspringbot.command.dashboard

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.properties.DashboardProperties
import uk.me.danielharman.kotlinspringbot.command.interfaces.IAdminCommand
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.models.Platform
import uk.me.danielharman.kotlinspringbot.security.DashboardService
import java.util.*

@Component
class LoginCommand(private val dashboardService: DashboardService): IAdminCommand {

    private val commandString = "login"

    override fun execute(event: PrivateMessageReceivedEvent) {
        when(val url = dashboardService.getUserLoginUrl(event.author.id, Platform.Discord)){
            is Failure -> event.channel.sendMessage(url.reason).queue()
            is Success -> event.channel.sendMessage(url.value).queue()
        }
    }

    override fun matchCommandString(str: String): Boolean = str == commandString
    override fun getCommandString(): String = commandString
}