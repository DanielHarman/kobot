package uk.me.danielharman.kotlinspringbot.security

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class DashboardUserService(private val dashboardUserRepository: DashboardUserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = dashboardUserRepository.findByUserId(username) ?: return null
        return User(user.userId, user.token, ArrayList())
    }
}