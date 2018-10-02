package ytpconnect.rocket.android.members.uimodel

import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.domain.GetSettingsInteractor
import ytpconnect.rocket.android.server.domain.baseUrl
import ytpconnect.rocket.common.model.User
import ytpconnect.rocket.core.model.Value
import javax.inject.Inject

class MemberUiModelMapper @Inject constructor(serverInteractor: GetCurrentServerInteractor, getSettingsInteractor: GetSettingsInteractor) {
    private var settings: Map<String, Value<Any>> = getSettingsInteractor.get(serverInteractor.get()!!)
    private val baseUrl = settings.baseUrl()

    fun mapToUiModelList(memberList: List<User>): List<MemberUiModel> {
        return memberList.map { MemberUiModel(it, settings, baseUrl) }
    }
}