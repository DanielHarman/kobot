package uk.me.danielharman.kotlinspringbot.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import uk.me.danielharman.kotlinspringbot.models.AudioClip

@Repository
interface AudioClipRepository : MongoRepository<AudioClip, String> {

    fun findOneByGuildIdAndClipName(guildId: String, clipName: String): AudioClip?
    fun findAllByGuildId(guildId: String, pageable: Pageable): Page<AudioClip>
    fun countByGuildId(guildId: String): Long

}