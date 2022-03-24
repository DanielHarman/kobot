package uk.me.danielharman.kotlinspringbot.security

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Document(collection = "dashboardUsers")
class DashboardUser(val userId: String, val token: String){
}

@Repository
interface DashboardUserRepository : MongoRepository<DashboardUser, String> {
    @Query(value = "{ 'platformIds': { \$elemMatch: { '_id': ?0, 'platform': ?1 } } }")
    fun findByPlatformId(id: String, platform: Platform): DashboardUser?
}
