package uk.me.danielharman.kotlinspringbot.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.OperationResult
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.properties.JwtProperties
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


/**
 * JWT Token service for voter auth, provides tokens that can be used to later identify a voter.
 */
@Service
class JWTTokenService(private val jwtProperties: JwtProperties) {
    private val algorithmHS = Algorithm.HMAC256(jwtProperties.secret)

    /**
     * Expiring JWT Token.
     *
     * @return a token that expires after the EXPIRATION_SEC variable with the claims specified
     */
    fun issue(userId: String, sessionToken: String): OperationResult<String, String> {
        return try {
            Success(
                JWT.create()
                    .withIssuer(jwtProperties.issuer)
                    .withClaim("userId", userId)
                    .withClaim("sessionToken", sessionToken)
                    .withExpiresAt(nowPlusSeconds())
                    .sign(algorithmHS)
            )
        } catch (exception: JWTCreationException) {
            Failure("JWT Creation Exception")
        }
    }

    /**
     * Verify a JWT token, Ensure it is valid and has not expired.
     *
     * @param token a JWT token
     * @return A voterUserForm containing the parsed claims
     */
    fun verify(token: String): OperationResult<String, String> {
        try {
            val verifier = JWT.require(algorithmHS)
                .acceptLeeway(1)
                .withIssuer(jwtProperties.issuer)
                .build()
            val jwt = verifier.verify(token)
            val claims = jwt.claims
//            form.setName(claims["name"]!!.asString())
//            form.setAddress(claims["address"]!!.asString())
//            form.setPostcode(claims["postcode"]!!.asString())
        } catch (exception: JWTVerificationException) {
            return Failure("JWT Verification Exception")
        }
        return Success("")
    }

    private fun nowPlusSeconds(): Date {
        return Date.from(
            LocalDateTime.now().plusSeconds(jwtProperties.expiry.toLong()).atZone(ZoneId.systemDefault()).toInstant()
        )
    }
}
