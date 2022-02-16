package uk.me.danielharman.kotlinspringbot.security

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import uk.me.danielharman.kotlinspringbot.models.Platform
import uk.me.danielharman.kotlinspringbot.models.PlatformId
import uk.me.danielharman.kotlinspringbot.models.dashboard.DashboardRole


@Document(collection = "dashboardUsers")
data class DashboardUser(val platformIds: MutableList<PlatformId> = mutableListOf(), val roles: MutableSet<DashboardRole> = mutableSetOf()) {
    @Id
    lateinit var id: ObjectId
    fun addRole(role: DashboardRole) = roles.add(role)
    fun removeRole(role: DashboardRole) = roles.remove(role)
}

@Repository
interface DashboardUserRepository : MongoRepository<DashboardUser, String> {
    @Query(value = "{ 'platformIds': { \$elemMatch: { '_id': ?0, 'platform': ?1 } } }")
    fun findByPlatformId(id: String, platform: Platform): DashboardUser?
}
