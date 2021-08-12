package uk.me.danielharman.kotlinspringbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import uk.me.danielharman.kotlinspringbot.properties.DiscordProperties
import uk.me.danielharman.kotlinspringbot.properties.SmmryPropeties

@SpringBootApplication
@EnableConfigurationProperties(DiscordProperties::class, SmmryPropeties::class)
class KotlinSpringBotApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringBotApplication>(*args)
}
