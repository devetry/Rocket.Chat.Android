package ytpconnect.rocket.android.server.domain

import ytpconnect.rocket.android.db.UserDao
import ytpconnect.rocket.android.db.model.UserEntity

class GetCurrentUserInteractor(
    private val tokenRepository: TokenRepository,
    private val currentServer: String,
    private val userDao: UserDao
) {
    fun get(): UserEntity? {
        return tokenRepository.get(currentServer)?.let {
            userDao.getUser(it.userId)
        }
    }

}
