package ytpconnect.rocket.android.server.domain

import ytpconnect.rocket.android.authentication.domain.model.TokenModel

interface MultiServerTokenRepository {
    fun get(server: String): TokenModel?

    fun save(server: String, token: TokenModel)

    fun clear(server: String)
}