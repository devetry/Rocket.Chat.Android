package ytpconnect.rocket.android.server.domain

import ytpconnect.rocket.common.model.User

interface ActiveUsersRepository {

    fun save(url: String, activeUsers: List<User>)

    fun get(url: String): List<User>
}