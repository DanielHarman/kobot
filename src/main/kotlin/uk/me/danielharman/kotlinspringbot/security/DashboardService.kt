package uk.me.danielharman.kotlinspringbot.security

import org.joda.time.DateTime
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.OperationResult
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.models.Platform
import uk.me.danielharman.kotlinspringbot.properties.DashboardProperties
import uk.me.danielharman.kotlinspringbot.services.JWTTokenService

@Service
class DashboardService(
    private val repository: DashboardUserRepository,
    private val issuedTokensRepository: IssuedTokensRepository,
    private val dashboardProperties: DashboardProperties,
    private val tokenService: JWTTokenService
    ) {

    fun getUser(id: String): OperationResult<DashboardUser, String> {
        val user = repository.findById(id)
        return if (user.isPresent) Success(user.get()) else Failure("User not found")
    }

    fun getUserByPlatformId(id: String, platform: Platform): OperationResult<DashboardUser, String> {
        val user = repository.findByPlatformId(id, platform) ?: return Failure("User not found")
        return Success(user)
    }

    fun getUserLoginUrl(id: String, platform: Platform): OperationResult<String, String> {
        return when (val user = getUserByPlatformId(id, platform)) {
            is Failure -> user
            is Success -> {

                val result = issuedTokensRepository.save(IssuedToken(DateTime.now(), user.value.id))

                Success("${dashboardProperties.rootAddress}/login?userId=${user.value.id}&token=${result.id}")
            }
        }
    }

    fun verifyLoginRequest(userId: String, token: String): OperationResult<String, String> {
        val tokenRequest = issuedTokensRepository.findById(token)

        if (tokenRequest.isEmpty || tokenRequest.get().expiryTime.isBeforeNow) {
            return Failure("Login token has expired")
        }

        return when (val user = getUser(userId)) {
            is Failure -> user
            is Success -> {
                when (val jwt = tokenService.issue(user.value.id.toHexString(), tokenRequest.get().id.toHexString())) {
                    is Failure -> Failure("Failed to issue token")
                    is Success -> {
                        issuedTokensRepository.deleteById(tokenRequest.get().id.toHexString())
                        jwt
                    }
                }
            }
        }
    }


}