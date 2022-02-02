package uk.me.danielharman.kotlinspringbot.models.dashboard

import org.bson.types.ObjectId
import uk.me.danielharman.kotlinspringbot.models.PlatformId


class DashboardUser(val platformIds: MutableList<PlatformId> = mutableListOf(), val roles: MutableSet<DashboardRole> = mutableSetOf()) {

    lateinit var id: ObjectId

    fun addRole(role: DashboardRole) = roles.add(role)
    fun removeRole(role: DashboardRole) = roles.remove(role)

}