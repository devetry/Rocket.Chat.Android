package ytpconnect.rocket.android.server.domain

interface TokenRepository : ytpconnect.rocket.core.TokenRepository {
    fun remove(url: String)
    fun clear()
}