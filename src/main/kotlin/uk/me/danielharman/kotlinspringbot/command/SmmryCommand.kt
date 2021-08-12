package uk.me.danielharman.kotlinspringbot.command

import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.command.interfaces.Command
import uk.me.danielharman.kotlinspringbot.command.interfaces.ISlashCommand
import uk.me.danielharman.kotlinspringbot.events.DiscordMessageEvent
import uk.me.danielharman.kotlinspringbot.models.CommandParameter
import uk.me.danielharman.kotlinspringbot.services.SmmryService

@Component
class SmmryCommand(private val smmryService: SmmryService): Command("summary", "Summarises the contents",
    listOf(
        CommandParameter(0, "Url", CommandParameter.ParamType.Word, "Url of the webpage to summarise", false),
        CommandParameter(1, "Length", CommandParameter.ParamType.Word, "How many sentences to show", false)
    )
), ISlashCommand {
    override fun execute(event: DiscordMessageEvent) {
        val urlVal = event.getParamValue(commandParameters[0])
        val lengthVal = event.getParamValue(commandParameters[1])

        val response = smmryService.getSummaryText(urlVal.value.toString(),lengthVal.value.toString().toInt())?.content

        event.reply(response!!)
    }
}