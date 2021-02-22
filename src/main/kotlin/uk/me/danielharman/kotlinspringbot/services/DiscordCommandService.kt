package uk.me.danielharman.kotlinspringbot.services

import me.xdrop.fuzzywuzzy.FuzzySearch
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import uk.me.danielharman.kotlinspringbot.models.DiscordCommand
import uk.me.danielharman.kotlinspringbot.repositories.DiscordCommandRepository
import java.io.InputStream
import java.lang.Integer.min
import kotlin.math.ceil

@Service
class DiscordCommandService(private val repository: DiscordCommandRepository, private val guildService: GuildService, private val attachmentService: AttachmentService, private val mongoTemplate: MongoTemplate) {

    fun commandCount(guildId: String): Long {
        guildService.getGuild(guildId) ?: return 0
        return repository.countByGuildId(guildId);
    }

    fun getCommands(guildId: String, page: Int = 0, pageSize: Int = 20, sort: Order = Order.asc("key")): List<DiscordCommand> {
        guildService.getGuild(guildId) ?: return listOf()
        return repository.findAllByGuildId(guildId, PageRequest.of(page, pageSize, Sort.by(sort))).toList()
    }

    fun getCommand(guildId: String, key: String): DiscordCommand? {
        guildService.getGuild(guildId) ?: return null
        return repository.findFirstByGuildIdAndKey(guildId, key)
    }

    fun createStringCommand(guildId: String, key: String, content: String, creatorId: String, overwrite: Boolean): DiscordCommand? {
        return createCommand(guildId, key, content, null, DiscordCommand.CommandType.STRING, creatorId, true)
    }

    fun createFileCommand(guildId: String, key: String, fileName: String, creatorId: String, inputStream: InputStream): DiscordCommand? {
        val command = createCommand(guildId, key, null, fileName, DiscordCommand.CommandType.FILE, creatorId, true)
                ?: return null
        attachmentService.saveFile(inputStream, guildId, fileName, key)
        return command
    }

    fun createCommand(guildId: String, key: String, content: String?, fileName: String?,
                      type: DiscordCommand.CommandType, creatorId: String, overwrite: Boolean): DiscordCommand? {
        guildService.getGuild(guildId) ?: return null

        val command: DiscordCommand? = getCommand(guildId, key)

        if (command == null || overwrite) {

            if (command != null && overwrite) {
                if (command.type == DiscordCommand.CommandType.FILE) {
                    attachmentService.deleteAttachment(guildId, command.fileName ?: "", command.key)
                }
                repository.deleteById(command.id)
            }

            return repository.save(DiscordCommand(guildId, key, content, fileName, type, creatorId))
        }
        return null
    }

    fun deleteCommand(guildId: String, key: String): Boolean {
        guildService.getGuild(guildId) ?: return false
        val command = getCommand(guildId, key) ?: return false
        repository.deleteById(command.id)
        if (command.type == DiscordCommand.CommandType.FILE) {
            attachmentService.deleteAttachment(guildId, command.fileName ?: "", command.key)
        }
        return true
    }

    private val CMD_PAGE_SIZE = 50

    fun searchCommand(guildId: String, searchTerm: String, limit: Int = 20 ): List<Pair<String, Int>> {

        val commandCount = commandCount(guildId)

        if (commandCount <= 0) return listOf()

        val commandList = mutableListOf<Pair<String, Int>>()

        val noOfPages = ceil(commandCount.toDouble() / CMD_PAGE_SIZE).toInt()

        //Paginated so that we aren't pulling 1000s of commands at a time if that ever happens
        for (page in 0 until noOfPages) {

            val query = Query(Criteria.where("guildId").`is`(guildId)).with(PageRequest.of(page, CMD_PAGE_SIZE))
            val cmds = mongoTemplate.find(query, DiscordCommand::class.java)

            commandList.addAll(cmds
                    .map { cmd -> Pair(cmd.key, FuzzySearch.ratio(searchTerm, cmd.key)) }
                    .filter { (_, ratio) -> ratio > 40 })

        }

        return commandList.sortedByDescending { (_, ratio) -> ratio }.subList(0, min(limit, commandList.size))
    }

}