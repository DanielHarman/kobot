package uk.me.danielharman.kotlinspringbot.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.properties.JwtProperties
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@Component
class JwtTokenUtil(private val jwtProperties: JwtProperties) {
    private val algorithmHS = Algorithm.HMAC256(jwtProperties.secret)
    fun generateAccessToken(user: DashboardUser): String {
        return JWT.create()
                .withIssuer(jwtProperties.issuer)
                .withSubject(user.userId)
                .withExpiresAt(nowPlusSeconds())
                .withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .sign(algorithmHS)
    }

    fun getUserId(token: String): String {
        JWT.require(algorithmHS).withIssuer(jwtProperties.issuer).build().apply {
            val verify = verify(token)
            return verify.subject
        }
    }

    fun getExpirationDate(token: String?): Date {
        JWT.require(algorithmHS).withIssuer(jwtProperties.issuer).build().apply {
            val verify = verify(token)
            return verify.expiresAt
        }
    }

    fun validate(token: String?): Boolean {
        try {
            JWT.require(algorithmHS).withIssuer(jwtProperties.issuer).build().apply {
                val verify = verify(token)
            }
        } catch (exception: JWTVerificationException){ return false }
        return true
    }

    private fun nowPlusSeconds(): Date {
        return Date.from(
                LocalDateTime.now().plusSeconds(jwtProperties.expiry.toLong()).atZone(ZoneId.systemDefault()).toInstant()
        )
    }
}