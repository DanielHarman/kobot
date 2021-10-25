package uk.me.danielharman.kotlinspringbot.command.administrators

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.command.interfaces.IAdminCommand
import uk.me.danielharman.kotlinspringbot.helpers.Embeds
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.security.OTPService

import uk.me.danielharman.kotlinspringbot.services.admin.AdministratorService

@Component
class GetOtpCommand(private val administratorService: AdministratorService, private val otpService: OTPService) : IAdminCommand {

    private val commandString = "login"

    override fun execute(event: PrivateMessageReceivedEvent) {

        when (val thisAdmin = administratorService.getBotAdministratorByDiscordId(event.author.id)){
            is Failure -> event.channel.sendMessageEmbeds(Embeds.createErrorEmbed("You are not an admin.")).queue()
            is Success -> {

                val otp = otpService.getOtp(event.author.id)

                event.channel.sendMessageEmbeds(
                    Embeds.infoEmbedBuilder().appendDescription(otp).build()
                ).queue()
            }
        }
    }

    override fun matchCommandString(str: String): Boolean = str == commandString
    override fun getCommandString(): String = commandString
}