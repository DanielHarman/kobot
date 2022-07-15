package uk.me.danielharman.kotlinspringbot.helpers

import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.guild.GenericGuildEvent

object JDAHelperFunctions {

    fun getBotVoiceChannel(event: GenericGuildEvent): AudioChannel? =
            event.guild.retrieveMemberById(event.jda.selfUser.id).complete()?.voiceState?.channel

    fun getAuthorIdFromMessageId(textChannel: TextChannel?, msgId: String): String =
            textChannel?.retrieveMessageById(msgId)?.complete()?.author?.id ?: ""

}