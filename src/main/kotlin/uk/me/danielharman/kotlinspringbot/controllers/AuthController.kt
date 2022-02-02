package uk.me.danielharman.kotlinspringbot.controllers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.services.JWTTokenService

@RestController
@RequestMapping(path = ["auth"])
@CrossOrigin(origins = ["https://localhost:5001"])
class AuthController(private val jwtTokenService: JWTTokenService) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    data class AuthRequest(val token: String, val userId: String)
    data class AuthResponse(val jwt: String)

    @PostMapping(produces = ["application/json"])
    fun authenticate(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        logger.info("${request.token}")
        val token = jwtTokenService.issue(request.userId, request.token)
        return when (token){
            is Failure -> ResponseEntity.internalServerError().build()
            is Success ->  ResponseEntity.ok(AuthResponse(token.value));
        }
    }

}