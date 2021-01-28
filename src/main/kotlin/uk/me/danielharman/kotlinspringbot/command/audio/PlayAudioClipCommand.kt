package uk.me.danielharman.kotlinspringbot.command.audio

import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.me.danielharman.kotlinspringbot.audio.NewAudioResultHandler
import uk.me.danielharman.kotlinspringbot.command.VoiceCommand
import uk.me.danielharman.kotlinspringbot.helpers.JDAHelperFunctions
import uk.me.danielharman.kotlinspringbot.provider.GuildMusicPlayerProvider
import uk.me.danielharman.kotlinspringbot.services.AudioClipService
import uk.me.danielharman.kotlinspringbot.services.GuildService

class PlayAudioClipCommand(
    private val guildMusicPlayerProvider: GuildMusicPlayerProvider,
    private val audioClipService: AudioClipService,
    private val guildService: GuildService,
    private val clipName: String
) : VoiceCommand {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override var voiceChannel: VoiceChannel? = null

    override fun execute(event: GuildMessageReceivedEvent) {

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
        val botVoiceChannel = JDAHelperFunctions.getBotVoiceChannel(event)
        if (botVoiceChannel != null) {
            logger.info("Bot's voice channel: " + botVoiceChannel.id)
            event.guild.audioManager.openAudioConnection(botVoiceChannel)
        }
        if (voiceChannel != event.guild.audioManager.connectedChannel) {
            logger.info("My voice channel: " + voiceChannel?.id)
            event.guild.audioManager.closeAudioConnection()
            event.guild.audioManager.openAudioConnection(voiceChannel)
        }

        val audioClip = audioClipService.getAudioClip(event.guild.id, clipName)

        if (audioClip == null) {
            event.channel.sendMessage("No such clip").queue()
            return
        }
        logger.info("Connected voice channel from manager: " + event.guild.audioManager.connectedChannel?.id)

        val musicManager = guildMusicPlayerProvider.getGuildAudioPlayer(voiceChannel!!.guild)
        guildMusicPlayerProvider.playerManager.loadItemOrdered(
            musicManager,
            audioClip.gridFsName,
            NewAudioResultHandler(voiceChannel, musicManager, event.channel, guildService)
        )

    }
}