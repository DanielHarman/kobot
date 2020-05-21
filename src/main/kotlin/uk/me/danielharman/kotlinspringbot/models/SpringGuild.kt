package uk.me.danielharman.kotlinspringbot.models

import org.springframework.data.annotation.Id

data class SpringGuild(private val guildId: String) {

    @Id
    private lateinit var id: String

    var wordCounts: HashMap<String, Int> = hashMapOf()
    var commandCounts: HashMap<String, Int> = hashMapOf()
    var userWordCounts: HashMap<String, Int> = hashMapOf()
    var savedCommands: HashMap<String, String> = hashMapOf()
    var privilegedUsers : List<String> = listOf()
    var logChannelId: String = ""
    var volume = 50

    override fun toString(): String {
        return "ChannelStats(guildId='$guildId', id='$id', wordCounts=$wordCounts, commandCounts=$commandCounts)"
    }
}