package uk.me.danielharman.kotlinspringbot.services

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.test.context.ActiveProfiles
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.helpers.assertFailure
import uk.me.danielharman.kotlinspringbot.helpers.assertSuccess
import uk.me.danielharman.kotlinspringbot.models.DiscordCommand
import uk.me.danielharman.kotlinspringbot.models.DiscordCommand.CommandType.FILE
import uk.me.danielharman.kotlinspringbot.models.DiscordCommand.CommandType.STRING
import uk.me.danielharman.kotlinspringbot.models.SpringGuild
import uk.me.danielharman.kotlinspringbot.repositories.DiscordCommandRepository
import uk.me.danielharman.kotlinspringbot.repositories.GuildRepository
import java.io.File

@SpringBootTest
@ActiveProfiles("test")
internal class DiscordCommandServiceTest(
    @Spy @Autowired private val guildRepo: GuildRepository,
    @Spy @Autowired private val repo: DiscordCommandRepository,
    @Spy @Autowired private val mongoTemplate: MongoTemplate,
    @Spy @Autowired private val gf: GridFsTemplate
) {

    @Mock
    private lateinit var springGuildService: SpringGuildService

    @Mock
    private lateinit var attachmentService: AttachmentService

    @InjectMocks
    private lateinit var service: DiscordCommandService

    @AfterEach
    fun tearDown() {
        repo.deleteAll()
    }

    @Test
    fun shouldCountCommands() {

        val springGuild = SpringGuild("1")
        Mockito.`when`(springGuildService.getGuild("1")).thenReturn(Success(springGuild))

        val commands = listOf(
            DiscordCommand("1", "test", null, null, STRING, ""),
            DiscordCommand("1", "test1", null, null, STRING, ""),
            DiscordCommand("1", "test2", null, null, STRING, ""),
            DiscordCommand("2", "test3", null, null, STRING, ""),
        )
        repo.saveAll(commands)
        val result = assertSuccess(service.commandCount("1"))
        assertEquals(3, result.value)
    }

    @Test
    fun shouldGetCommandsPaginated() {
        val springGuild = SpringGuild("1")
        Mockito.`when`(springGuildService.getGuild("1")).thenReturn(Success(springGuild))

        val commands = listOf(
            DiscordCommand("1", "test", null, null, STRING, ""),
            DiscordCommand("1", "test1", null, null, STRING, ""),
            DiscordCommand("1", "test2", null, null, STRING, ""),
            DiscordCommand("1", "test3", null, null, STRING, ""),
        )
        repo.saveAll(commands)

        val result1 = assertSuccess(service.getCommands("1", 0, 2))
        val result2 = assertSuccess(service.getCommands("1", 1, 2))

        assertThat(
            result1.value, containsInAnyOrder(
                allOf(
                    hasProperty("key", equalTo("test")),
                    hasProperty("guildId", equalTo("1"))
                ),
                allOf(
                    hasProperty("key", equalTo("test1")),
                    hasProperty("guildId", equalTo("1"))
                )
            )
        )
        assertThat(
            result2.value, containsInAnyOrder(
                allOf(
                    hasProperty("key", equalTo("test2")),
                    hasProperty("guildId", equalTo("1"))
                ),
                allOf(
                    hasProperty("key", equalTo("test3")),
                    hasProperty("guildId", equalTo("1"))
                )
            )
        )
    }

    @Test
    fun shouldGetCommandsPaginatedByType() {
        val springGuild = SpringGuild("1")
        Mockito.`when`(springGuildService.getGuild("1")).thenReturn(Success(springGuild))

        val commands = listOf(
            DiscordCommand("1", "test", null, null, STRING, ""),
            DiscordCommand("1", "test1", null, null, STRING, ""),
            DiscordCommand("1", "test2", null, null, FILE, ""),
            DiscordCommand("1", "test3", null, null, FILE, ""),
        )
        repo.saveAll(commands)

        val result1 = assertSuccess(service.getCommands("1", 0, 20, STRING))
        val result2 = assertSuccess(service.getCommands("1", 0, 20, FILE))

        assertThat(result1.value.size, equalTo(2));
        assertThat(result2.value.size, equalTo(2));

        assertThat(
            result1.value, containsInAnyOrder(
                allOf(
                    hasProperty("key", equalTo("test")),
                    hasProperty("guildId", equalTo("1"))
                ),
                allOf(
                    hasProperty("key", equalTo("test1")),
                    hasProperty("guildId", equalTo("1"))
                )
            )
        )
        assertThat(
            result2.value, containsInAnyOrder(
                allOf(
                    hasProperty("key", equalTo("test2")),
                    hasProperty("guildId", equalTo("1"))
                ),
                allOf(
                    hasProperty("key", equalTo("test3")),
                    hasProperty("guildId", equalTo("1"))
                )
            )
        )
    }

    @Test
    fun shouldNotGetLeakRandomCommand(){
        val springGuild = SpringGuild("1")
        Mockito.`when`(springGuildService.getGuild("1")).thenReturn(Success(springGuild))

        repo.save(DiscordCommand("2", "incorrect", null, null, STRING, ""))

        assertFailure(service.getRandomCommand("1"))
    }

    @Test
    fun shouldExportJson(){
        val springGuild = SpringGuild("1")
        Mockito.`when`(springGuildService.getGuild("1")).thenReturn(Success(springGuild))
        val commands = listOf(
            DiscordCommand("1", "test", "this is a test command", null, STRING, ""),
            DiscordCommand("1", "test1", "cheese", null, STRING, ""),
            DiscordCommand("1", "test2", null, "image.jpg", FILE, ""),
            DiscordCommand("1", "test3", null, "loud.mp4", FILE, ""),
        )
        repo.saveAll(commands)

        attachmentService.saveFile(File("/home/cogboy/Downloads/RDT_20220513_2235464581588268984549158.webp").inputStream(), "1", "image.jpg", "test2")
        attachmentService.saveFile(File("/home/cogboy/Downloads/RDT_20220513_2235464581588268984549158.webp").inputStream(), "1", "loud.mp4", "test3")

        val res = service.getCommandsJson("1234", "1", true, true)
        println(res)
        service.exportCommands("1234", "1")
    }


}