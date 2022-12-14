package uk.me.danielharman.kotlinspringbot.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*
import uk.me.danielharman.kotlinspringbot.helpers.Failure
import uk.me.danielharman.kotlinspringbot.helpers.Success
import uk.me.danielharman.kotlinspringbot.services.SpringGuildService
import uk.me.danielharman.kotlinspringbot.services.admin.AdministratorService
import java.text.SimpleDateFormat
import java.util.stream.Collectors

@RestController
@CrossOrigin(origins = ["https://localhost:5001"])
class ApiController(
    private val springGuildService: SpringGuildService,
    private val administratorService: AdministratorService
) {

    private val mapper: ObjectMapper = ObjectMapper()

    init {
        mapper.registerModule(JodaModule())
        mapper.dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
    }

    @GetMapping("/api/admin/bot/startTime", produces = ["application/json"])
    fun getBotStartTime(): ResponseEntity<String>{
        return when (val botStartTime = administratorService.getBotStartTime()){
            is Failure -> badRequest().body(mapper.writeValueAsString(botStartTime))
            is Success -> ok(mapper.writeValueAsString(botStartTime))
        }
    }

    @GetMapping("/api/admin/discord/startTime", produces = ["application/json"])
    fun getDiscordStartTime(): ResponseEntity<String>{
        return when (val botStartTime = administratorService.getDiscordStartTime()){
            is Failure -> badRequest().body(mapper.writeValueAsString(botStartTime))
            is Success -> ok(mapper.writeValueAsString(botStartTime))
        }
    }

    @GetMapping("/api/guilds", produces = ["application/json"])
    fun getGuilds(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) pageSize: Int?
    ): ResponseEntity<String> {

        return if (page == null && pageSize != null)
            ok(mapper.writeValueAsString(springGuildService.getGuilds(pageSize = pageSize)))
        else if (pageSize == null && page != null)
            ok(mapper.writeValueAsString(springGuildService.getGuilds(page = page)))
        else
            ok(mapper.writeValueAsString(springGuildService.getGuilds()))
    }

    @GetMapping("/api/guilds/{id}", produces = ["application/json"])
    fun getGuild(@PathVariable id: String): ResponseEntity<String> {
        val guild = springGuildService.getGuild(id) ?: return notFound().build()
        return ok(mapper.writeValueAsString(guild))
    }

    @GetMapping("/api/guilds/{id}/commands", produces = ["application/json"])
    fun getGuildCommands(@PathVariable id: String): ResponseEntity<String> {
        val getGuild = springGuildService.getGuild(id)
        if (getGuild is Failure) return badRequest().build()
        val guild = (getGuild as Success).value

        val collect = guild.customCommands.entries.stream()
            .map { kp ->
                run {
                    kp.value.keyword = kp.key
                    kp.value
                }
            }
            .collect(Collectors.toList())

        return ok(mapper.writeValueAsString(collect))
    }

    @GetMapping("/api/guilds/{guildId}/commands/{id}", produces = ["application/json"])
    fun getGuildCommand(@PathVariable guildId: String, @PathVariable id: String): ResponseEntity<String> {
        val getGuild = springGuildService.getGuild(id)
        if (getGuild is Failure) return badRequest().build()
        val guild = (getGuild as Success).value

        if (guild.customCommands.isEmpty())
            return badRequest().build()

        return ok(mapper.writeValueAsString(guild.customCommands.values.shuffled().take(1)[0]))
    }

}