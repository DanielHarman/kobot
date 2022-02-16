package uk.me.danielharman.kotlinspringbot.security

import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.KotlinBotProperties
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.OperationResult
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.models.Platform
import uk.me.danielharman.kotlinspringbot.models.PlatformId
import uk.me.danielharman.kotlinspringbot.models.SpringGuild
import uk.me.danielharman.kotlinspringbot.models.dashboard.DashboardRole
import uk.me.danielharman.kotlinspringbot.objects.ApplicationInfo
import uk.me.danielharman.kotlinspringbot.properties.DashboardProperties
import uk.me.danielharman.kotlinspringbot.services.DiscordService
import uk.me.danielharman.kotlinspringbot.services.SpringGuildService
import java.util.*

@Service
class DashboardService(
    private val repository: DashboardUserRepository,
    private val issuedTokensRepository: IssuedTokensRepository,
    private val dashboardProperties: DashboardProperties,
    private val mongoOperations: MongoOperations
) {

    fun getUserByPlatformId(id: String, platform: Platform): OperationResult<DashboardUser, String>{
        val user = repository.findByPlatformId(id, platform) ?: return Failure("User not found")
        return Success(user)
    }

    fun getUserLoginUrl(id: String, platform: Platform): OperationResult<String,String> {
        return when(val user = getUserByPlatformId(id, platform)){
            is Failure -> user
            is Success -> {

                val result = issuedTokensRepository.save(IssuedToken(DateTime.now(), user.value.id))

                Success("${dashboardProperties.rootAddress}/login?userId=${user.value.id}&token=${result.id}")
            }
        }
    }

}