package uk.me.danielharman.kotlinspringbot

import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import uk.me.danielharman.kotlinspringbot.objects.ApplicationInfo
import uk.me.danielharman.kotlinspringbot.objects.DiscordObject
import uk.me.danielharman.kotlinspringbot.security.DashboardUser
import uk.me.danielharman.kotlinspringbot.security.DashboardUserRepository
import uk.me.danielharman.kotlinspringbot.services.*
import uk.me.danielharman.kotlinspringbot.services.admin.AdministratorService
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
@Profile("!test")
class SetupService(
    private val userRepository: DashboardUserRepository,
    private val env: Environment,
    private val mongoOperations: MongoOperations,
    private val discordService: DiscordService,
    private val buildProperties: BuildProperties,
    private val kotlinBotProperties: KotlinBotProperties,
    private val administratorService: AdministratorService,
    private val listeners: List<ListenerAdapter>
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun setup() {
        logger.info("Running bot version ${buildProperties.version}")

        SchemaUpdater(mongoOperations).updateSchema()

        val activeProfiles = env.activeProfiles

        val defaultUser = userRepository.findByUsername("admin")
        //Setup dashboard user
        if (defaultUser == null) {
            logger.info("########################")
            logger.info("No default user found. Creating a new user.")
            val defaultUsername = "admin"
            val randomUUID = UUID.randomUUID().toString()
            logger.info("Username: $defaultUsername Password: $randomUUID")
            userRepository.save(DashboardUser(defaultUsername, BCryptPasswordEncoder().encode(randomUUID)))
            logger.info("########################")
        }

        if (activeProfiles.contains("dev")) {
            val devUser = userRepository.findByUsername("dev")
            //Setup dashboard user
            if (devUser == null) {
                logger.info("########################")
                logger.info("No dev user found. Creating a new user.")
                val defaultUsername = "dev"
                val password = "password"
                logger.info("Username: $defaultUsername Password: $password")
                userRepository.save(DashboardUser(defaultUsername, BCryptPasswordEncoder().encode(password)))
                logger.info("########################")
            }
        }

        //Create default admin
        administratorService.createBotAdministrator(kotlinBotProperties.primaryPrivilegedUserId)

        if (!activeProfiles.contains("discordDisabled")) {
            //Injecting listeners here otherwise we'll get circular dependencies
            DiscordObject.registerListeners(listeners)
            logger.info(discordService.startDiscordConnection().value)
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    discordService.sendLatestXkcd()
                }
            }, 3000, 10800000) // Start after 3 seconds, check every 3hrs

        } else {
            logger.info("Running with Discord disabled")
        }

        //Kotlin objects are lazy
        ApplicationInfo.startTime = DateTime.now()
        ApplicationInfo.version = buildProperties.version
    }

    @PreDestroy
    fun destroy() {
        logger.info("Cleaning up for shutdown")
        logger.info(discordService.closeDiscordConnection().message)
        logger.info("Cleanup complete")
    }

}