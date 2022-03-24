package uk.me.danielharman.kotlinspringbot.controllers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["auth"])
@CrossOrigin(origins = ["https://localhost:5001"])
class AuthController(private val dashboardUserService: DashboardService) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    data class AuthRequest(val token: String, val userId: String)
    data class AuthResponse(val jwt: String, val message: String? = null)

    @PostMapping(produces = ["application/json"])
    fun authenticate(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        return when (val token = dashboardUserService.verifyLoginRequest(request.userId, request.token)){
            is Failure -> ResponseEntity.badRequest().body(AuthResponse("", token.reason))
            is Success ->  ResponseEntity.ok(AuthResponse(token.value));
        }
    }

}