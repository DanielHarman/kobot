package uk.me.danielharman.kotlinspringbot.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.command.interfaces.ICommand
import uk.me.danielharman.kotlinspringbot.helpers.Comparators
import uk.me.danielharman.kotlinspringbot.services.GuildService

@Component
class UserStatsCommand (private val guildService: GuildService): ICommand {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val commandString = "userstats"
    private val description = "List member word counts"

    override fun matchCommandString(str: String): Boolean = str == commandString

    override fun getCommandString(): String = commandString

    override fun getCommandDescription(): String = description

    override fun execute(event: GuildMessageReceivedEvent) {

        val guildId = event.message.guild.id
        val guildName = event.message.guild.name
        val springGuild = guildService.getGuild(guildId)

        val message = if (springGuild == null) {
            EmbedBuilder().addField("error", "Could not find stats for server", false).build()
        } else {

            val stringBuilder = StringBuilder()

            springGuild.userWordCounts.entries
                    .stream()
                    .sorted(Comparators.mapStrIntComparator)
                    .limit(20)
                    .forEach { (s, i) ->
                        run {
                            try {
                                stringBuilder.append("${event.message.guild.retrieveMemberById(s).complete()?.nickname ?: s} - $i words\n")
                            } catch (e: ErrorResponseException)
                            {
                                logger.error("Failed to find user $s by id")
                            }
                        }
                    }

            EmbedBuilder()
                    .appendDescription(stringBuilder.toString())
                    .setColor(0x9d03fc)
                    .setTitle("Words said per user for $guildName")
                    .build()
        }

        event.channel.sendMessage(message).queue()
    }
}