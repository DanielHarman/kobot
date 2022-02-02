package uk.me.danielharman.kotlinspringbot.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("dashboard")
class DashboardProperties(var rootAddress: String)