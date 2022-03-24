package uk.me.danielharman.kotlinspringbot.controllers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["auth"])
@CrossOrigin(origins = ["https://localhost:5001"])
class AuthController(private val authenticationManager: AuthenticationManager) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    data class AuthRequest(val token: String, val userId: String)
    data class AuthResponse(val jwt: String, val message: String? = null)

    @PostMapping("/login")
    fun authenticate(@RequestBody request: AuthRequest): AuthResponse {

        val token = UsernamePasswordAuthenticationToken(request.userId, request.token)

        val authResult = authenticationManager.authenticate(token)

        return AuthResponse(true.toString())

    }

}