package uk.me.danielharman.kotlinspringbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import uk.me.danielharman.kotlinspringbot.properties.DashboardProperties
import uk.me.danielharman.kotlinspringbot.properties.JwtProperties

@SpringBootApplication
@EnableConfigurationProperties(KotlinBotProperties::class, DashboardProperties::class, JwtProperties::class)
class KotlinSpringBotApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringBotApplication>(*args)
}
