package uk.me.danielharman.kotlinspringbot.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import uk.me.danielharman.kotlinspringbot.helpers.Embeds
import uk.me.danielharman.kotlinspringbot.services.DiscordCommandService
import uk.me.danielharman.kotlinspringbot.services.GuildService
import kotlin.math.ceil

class FetchSavedCommand(private val guildService: GuildService, private val commandService: DiscordCommandService) : Command {

    private val MAX_PAGE_SIZE = 20

    override fun execute(event: GuildMessageReceivedEvent) {
        val guild = guildService.getGuild(event.guild.id)
        if (guild == null) {
            event.channel.sendMessage(Embeds.createErrorEmbed("Guild not found")).queue()
            return
        }

        val split = event.message.contentStripped.split(" ")
        val page = if (split.size < 2) 1 else split[1].toIntOrNull() ?: 1

        val commandCount = commandService.commandCount(guild.guildId)

        val pages = ceil((commandCount.toDouble() / MAX_PAGE_SIZE)).toInt()

        if (page < 1 || page > pages) {
            event.channel.sendMessage(Embeds.createErrorEmbed("$page is not a valid page number, choose between 1 and $pages")).queue()
            return
        }

        val commandList = commandService.getCommands(guild.guildId, page - 1, MAX_PAGE_SIZE)

        val builder = EmbedBuilder()
                .setTitle("Saved commands")
                .setDescription("Page $page of $pages ($commandCount saved commands)")
                .setColor(0x9d03fc)

        commandList.forEach { cmd ->
            builder.addField(cmd.key, truncate(cmd.content ?: cmd.fileName ?: "No Content", 30), true)
        }

        event.channel.sendMessage(builder.build()).queue()
    }


    private fun truncate(str: String, limit: Int): String =
            if (str.length <= limit) str else str.slice(IntRange(0, limit))

}