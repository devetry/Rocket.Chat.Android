package ytpconnect.rocket.android.authentication.domain.model

import ytpconnect.rocket.common.model.Token
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class TokenModel(val userId: String, val authToken: String)

fun TokenModel.toToken() = Token(userId, authToken)