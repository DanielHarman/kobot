package uk.me.danielharman.kotlinspringbot.security

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Document(collection = "dashboardUsers")
class DashboardUser(val userId: String, val token: String){
}

@Repository
interface DashboardUserRepository : MongoRepository<DashboardUser, String> {
    fun findByUserId(userId: String): DashboardUser
}
