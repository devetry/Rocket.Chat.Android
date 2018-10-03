package ytpconnect.rocket.android.authentication.infraestructure

import ytpconnect.rocket.android.authentication.domain.model.TokenModel
import ytpconnect.rocket.android.dagger.scope.PerActivity
import ytpconnect.rocket.android.infrastructure.LocalRepository
import ytpconnect.rocket.android.infrastructure.LocalRepository.Companion.TOKEN_KEY
import ytpconnect.rocket.android.server.domain.MultiServerTokenRepository
import com.squareup.moshi.Moshi

@PerActivity
class SharedPreferencesMultiServerTokenRepository(private val repository: LocalRepository,
                                                  private val moshi: Moshi
) : MultiServerTokenRepository {

    override fun get(server: String): TokenModel? {
        val token = repository.get("$TOKEN_KEY$server")
        val adapter = moshi.adapter<TokenModel>(TokenModel::class.java)

        token?.let {
            return adapter.fromJson(token)
        }

        return null
    }

    override fun save(server: String, token: TokenModel) {
        val adapter = moshi.adapter<TokenModel>(TokenModel::class.java)

        repository.save("$TOKEN_KEY$server", adapter.toJson(token))
    }

    override fun clear(server: String) {
        repository.clear("$TOKEN_KEY$server")
    }
}