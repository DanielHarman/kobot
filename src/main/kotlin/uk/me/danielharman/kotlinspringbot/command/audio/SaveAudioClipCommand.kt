package uk.me.danielharman.kotlinspringbot.command.audio

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import uk.me.danielharman.kotlinspringbot.command.Command
import uk.me.danielharman.kotlinspringbot.helpers.Embeds
import uk.me.danielharman.kotlinspringbot.services.AudioClipService

class SaveAudioClipCommand(private val audioClipService: AudioClipService) : Command {
    override fun execute(event: GuildMessageReceivedEvent) {
        val content = event.message.contentRaw
        val split = content.split(" ")

        if (split.size < 3 && event.message.attachments.size <= 0) {
            event.message.channel.sendMessage(Embeds.createErrorEmbed("Content missing")).queue()
            return
        }
        val attachment = event.message.attachments[0]
        audioClipService.saveAudioClip(
            event.message.guild.id,
            split[1],
            attachment.fileName,
            attachment.retrieveInputStream().get()
        )

        event.message.channel.sendMessage(Embeds.infoEmbedBuilder().setDescription("Saved clip as ${split[1]}").build())
            .queue()
    }
}