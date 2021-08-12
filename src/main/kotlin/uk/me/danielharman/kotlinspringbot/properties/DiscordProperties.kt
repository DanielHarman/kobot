package uk.me.danielharman.kotlinspringbot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("discord")
data class DiscordProperties(
        var commandPrefix: String,
        var voiceCommandPrefix: String,
        var privilegedCommandPrefix: String,
        var primaryPrivilegedUserId: String,
        var token: String
)