package uk.me.danielharman.kotlinspringbot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import uk.me.danielharman.kotlinspringbot.listeners.MessageListener

//TODO re-write because dumb things because java inline class overrides
class NewAudioResultHandler(val voiceChannel: VoiceChannel?, val musicManager: GuildMusicManager, val channel: TextChannel, val parent: MessageListener): AudioLoadResultHandler {


    override fun trackLoaded(track: AudioTrack) {
        channel.sendMessage("Adding to queue ${track.info.title}")
        parent.play(voiceChannel, channel.guild, musicManager, track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        var firstTrack = playlist.selectedTrack

        if (firstTrack == null){
            firstTrack = playlist.tracks[0]
        }

        channel.sendMessage("Adding to queue ${firstTrack.info.title} (first track of playlist ${playlist.name}")
        parent.play(voiceChannel, channel.guild, musicManager, firstTrack)

    }

    override fun noMatches() {
        channel.sendMessage("Nothing found by").queue();
    }

    override fun loadFailed(exception: FriendlyException) {
        channel.sendMessage("Could not play: ${exception.message}").queue()
    }

}