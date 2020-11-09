package uk.me.danielharman.kotlinspringbot.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.command.*
import uk.me.danielharman.kotlinspringbot.command.admin.*

@Service
class AdminCommandService(val guildService: GuildService) {

    @Value("\${discord.primaryPrivilegedUserId}")
    private lateinit var primaryAdminId: String

    fun getCommand(command: String): Command {
        return when (command) {
            "ping" -> PingCommand()
            "addadmin" -> AddAdminCommand(guildService)
            "removeadmin" -> RemoveAdminCommand(guildService)
            "admins" -> AdminsListCommand(guildService, primaryAdminId)
            "purge" -> PurgeMessagesCommand()
            "addmemechannel" -> AddMemeChannelCommand(guildService)
            "removememechannel" -> RemoveMemeChannelCommand(guildService)
            "setXkcdChannel" -> SetXkcdChannelCommand(guildService)
            "setnick" -> SetNickCommand()
            "deafen" -> ChannelDeafenCommand(guildService)
            "undeafen" -> ChannelUnDeafenCommand(guildService)
            "isdeafened" -> IsChannelDeafenedCommand(guildService)
            else -> DefaultCommand(command);
        }
    }

}