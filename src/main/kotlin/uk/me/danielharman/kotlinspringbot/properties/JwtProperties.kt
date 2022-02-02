package uk.me.danielharman.kotlinspringbot.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("jwt")
class JwtProperties(val issuer: String, val secret: String, val expiry: Int)