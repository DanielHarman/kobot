package uk.me.danielharman.kotlinspringbot.security

import org.bson.types.ObjectId
import org.joda.time.DateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Document(collection = "issuedTokens")
data class IssuedToken(val issuedTime: DateTime, val userId: ObjectId){
    @Id
    lateinit var id: ObjectId
    var expiryTime: DateTime = issuedTime.plusMinutes(5)
}

@Repository
interface IssuedTokensRepository: MongoRepository<IssuedToken, String> {
}