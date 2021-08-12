package uk.me.danielharman.kotlinspringbot.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("smmry")
data class SmmryPropeties(
    val endpoint: String,
    val apiKey: String
)
