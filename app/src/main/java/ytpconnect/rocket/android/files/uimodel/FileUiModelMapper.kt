package ytpconnect.rocket.android.files.uimodel

import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.domain.GetSettingsInteractor
import ytpconnect.rocket.android.server.domain.TokenRepository
import ytpconnect.rocket.android.server.domain.baseUrl
import ytpconnect.rocket.core.model.Value
import ytpconnect.rocket.core.model.attachment.GenericAttachment
import javax.inject.Inject

class FileUiModelMapper @Inject constructor(
    serverInteractor: GetCurrentServerInteractor,
    getSettingsInteractor: GetSettingsInteractor,
    private val tokenRepository: TokenRepository
) {
    private var settings: Map<String, Value<Any>> =
        getSettingsInteractor.get(serverInteractor.get()!!)
    private val baseUrl = settings.baseUrl()

    fun mapToUiModelList(fileList: List<GenericAttachment>): List<FileUiModel> {
        return fileList.map { FileUiModel(it, settings, tokenRepository, baseUrl) }
    }
}