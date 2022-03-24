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

    fun getUserLoginUrl(id: String): OperationResult<String, String> {
        return when (val user = getUser(id)) {
            is Failure -> user
            is Success -> {
                Success("${dashboardProperties.rootAddress}/login?userId=${user.value.userId}&token=${user.value.token}")
            }
        }
    }
}