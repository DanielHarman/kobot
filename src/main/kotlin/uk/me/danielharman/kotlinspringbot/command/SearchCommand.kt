package uk.me.danielharman.kotlinspringbot.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.command.interfaces.ICommand
import uk.me.danielharman.kotlinspringbot.helpers.Embeds
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.services.DiscordCommandService

@Component
class SearchCommand(private val commandService: DiscordCommandService) : ICommand {

    private val commandString = "search"
    private val description = "Search for commands"

    override fun matchCommandString(str: String): Boolean = str == commandString

    override fun getCommandString(): String = commandString

    override fun getCommandDescription(): String = description

    override fun execute(event: GuildMessageReceivedEvent) {

        val split = event.message.contentStripped.split(' ')

        if (split.size <= 1) {
            event.channel.sendMessage(Embeds.createErrorEmbed("No search term given.")).queue()
            return
        }

        val builder = EmbedBuilder()
            .setTitle("Matched commands")
            .setColor(0x9d03fc)

        when(val searchCommand = commandService.searchCommand(event.guild.id, split[1])){
            is Failure -> builder.setDescription("No matching commands found.")
            is Success -> {
                builder.appendDescription("Command - Closeness to ${split[1]}\n\n")
                searchCommand.value.forEach { cmd ->
                    builder.appendDescription("${cmd.first} - ${cmd.second}%\n")
                }
            }
        }

        event.channel.sendMessage(builder.build()).queue()
    }

}