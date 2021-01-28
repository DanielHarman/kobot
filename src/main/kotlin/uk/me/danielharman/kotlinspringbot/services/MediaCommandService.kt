package uk.me.danielharman.kotlinspringbot.services

import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.command.Command
import uk.me.danielharman.kotlinspringbot.command.PlayMusicCommand
import uk.me.danielharman.kotlinspringbot.command.audio.PlayAudioClipCommand
import uk.me.danielharman.kotlinspringbot.command.audio.SaveAudioClipCommand
import uk.me.danielharman.kotlinspringbot.provider.GuildMusicPlayerProvider

@Service
class MediaCommandService(
    private val guildMusicPlayerProvider: GuildMusicPlayerProvider,
    private val guildService: GuildService,
    private val audioClipService: AudioClipService
) {

    fun getCommand(command: String): Command {
        return when (command) {
            "clip" -> SaveAudioClipCommand(audioClipService)
            "play" -> PlayMusicCommand(guildMusicPlayerProvider, guildService)
            else -> PlayAudioClipCommand(guildMusicPlayerProvider, audioClipService, guildService, command)
        }

    }
}