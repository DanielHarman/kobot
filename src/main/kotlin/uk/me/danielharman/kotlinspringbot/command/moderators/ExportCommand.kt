package uk.me.danielharman.kotlinspringbot.command.moderators

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.command.interfaces.IModeratorCommand
import uk.me.danielharman.kotlinspringbot.services.DiscordCommandService
import uk.me.danielharman.kotlinspringbot.services.SpringGuildService

@Component
class ExportCommand(private val springGuildService: SpringGuildService, private val discordCommandService: DiscordCommandService) : IModeratorCommand {

    private val commandString: String = "export"

    override fun matchCommandString(str: String): Boolean = commandString == str

    override fun getCommandString(): String = commandString

    override fun execute(event: GuildMessageReceivedEvent) {

        val ins = discordCommandService.exportCommands(event.author.id, event.guild.id, false)
        event.channel.sendFile(ins, "export.zip").queue()
    }
}