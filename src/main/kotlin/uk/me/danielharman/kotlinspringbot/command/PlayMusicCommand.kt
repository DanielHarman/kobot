package uk.me.danielharman.kotlinspringbot.command

import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import uk.me.danielharman.kotlinspringbot.objects.ApplicationLogger
import uk.me.danielharman.kotlinspringbot.audio.NewAudioResultHandler
import uk.me.danielharman.kotlinspringbot.helpers.JDAHelperFunctions.getBotVoiceChannel
import uk.me.danielharman.kotlinspringbot.provider.GuildMusicPlayerProvider
import uk.me.danielharman.kotlinspringbot.services.GuildService

class PlayMusicCommand(private val guildMusicPlayerProvider: GuildMusicPlayerProvider, private val guildService: GuildService): VoiceCommand {
    override var voiceChannel: VoiceChannel? = null

    override fun execute(event: GuildMessageReceivedEvent) {

        val split = event.message.contentStripped.split(" ")
        if (split.size < 2) {
            val player = guildMusicPlayerProvider.getGuildAudioPlayer(event.guild).player
            player.isPaused = !player.isPaused

            val message = if (player.isPaused) "Paused" else "Playing"
            event.channel.sendMessage(message).queue()
            return
        }

        val member = event.member

        if (member == null) {
            event.channel.sendMessage("Can't find member!!!!").queue()
            return
        }

        val voiceState = member.voiceState

        if (voiceState == null) {
            event.channel.sendMessage("Can't find member voicestate! Are you in a channel?").queue()
            return
        }

        voiceChannel = voiceState.channel

        if (voiceChannel == null) {
            event.channel.sendMessage("Can't find voice channel! Are you in a channel?").queue()
            return
        }
        val botVoiceChannel= getBotVoiceChannel(event)
        if(botVoiceChannel != null)
        {
            ApplicationLogger.logger.info("Bot's voice channel: " + botVoiceChannel?.id)
            event.guild.audioManager.openAudioConnection(botVoiceChannel)
        }
        if(voiceChannel != event.guild.audioManager.connectedChannel)
        {
            ApplicationLogger.logger.info("My voice channel: " + voiceChannel?.id)
            event.guild.audioManager.closeAudioConnection()
            event.guild.audioManager.openAudioConnection(voiceChannel)
        }
        ApplicationLogger.logger.info("Connected voice channel from manager: " + event.guild.audioManager.connectedChannel?.id)
        val musicManager = guildMusicPlayerProvider.getGuildAudioPlayer(voiceChannel!!.guild)
        guildMusicPlayerProvider.playerManager.loadItemOrdered(musicManager, split[1], NewAudioResultHandler(voiceChannel, musicManager, event.channel, guildService))
    }
}