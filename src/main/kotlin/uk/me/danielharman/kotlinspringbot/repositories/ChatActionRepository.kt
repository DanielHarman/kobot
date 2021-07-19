package uk.me.danielharman.kotlinspringbot.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import uk.me.danielharman.kotlinspringbot.models.ChatAction

@Repository
interface ChatActionRepository : MongoRepository<ChatAction, String> {

    fun findAllByOriginId(originId: String): List<ChatAction>
    fun findByOriginIdAndMatchValue(originId: String, matchValue: String): ChatAction?
    fun deleteByOriginIdAndMatchValue(originId: String, matchValue: String)

}