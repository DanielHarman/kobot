package uk.me.danielharman.kotlinspringbot.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import uk.me.danielharman.kotlinspringbot.services.MemeService
import java.awt.Color

class GetMemeRank(val memeService: MemeService) : Command {
    override fun execute(event: GuildMessageReceivedEvent) {

        val split = event.message.contentStripped.split(' ')
        val asc = (split.size > 1 && split[1] == "asc" )
        val memerIds = memeService.getMemerIds(event.guild.id, asc)
        val des = StringBuilder()

        var counter = 1
        memerIds.forEach {
            pair ->
            val name = event.guild.getMemberById(pair.first)?.nickname
                    ?: event.jda.getUserById(pair.first)?.asTag
                    ?: pair.first
            des.append("${if(!asc) "#" else ""}${if (!asc) counter++.toString() else ""} $name: S:${pair.second.score}   U:${pair.second.upvotes}   D:${pair.second.downvotes} \n")
        }
        event.channel.sendMessage(EmbedBuilder()
                .setTitle(if (asc) "Losers in ${event.guild.name}" else "Meme ranking for ${event.guild.name}")
                .setColor(Color.GRAY)
                .appendDescription(des.toString()).build()).queue()
    }
}