package uk.me.danielharman.kotlinspringbot.command

import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.command.interfaces.Command
import uk.me.danielharman.kotlinspringbot.command.interfaces.ISlashCommand
import uk.me.danielharman.kotlinspringbot.events.DiscordMessageEvent

@Component
class PingCommand : Command("a", "ping"), ISlashCommand {
    override fun execute(event: DiscordMessageEvent) = event.reply(">a")
}