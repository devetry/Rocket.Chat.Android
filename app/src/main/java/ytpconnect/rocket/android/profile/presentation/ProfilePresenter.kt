package ytpconnect.rocket.android.profile.presentation

import android.graphics.Bitmap
import android.net.Uri
import ytpconnect.rocket.android.chatroom.domain.UriInteractor
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.helper.UserHelper
import ytpconnect.rocket.android.server.domain.GetCurrentServerInteractor
import ytpconnect.rocket.android.server.infraestructure.RocketChatClientFactory
import ytpconnect.rocket.android.util.extension.compressImageAndGetByteArray
import ytpconnect.rocket.android.util.extension.launchUI
import ytpconnect.rocket.android.util.extensions.avatarUrl
import ytpconnect.rocket.android.util.retryIO
import ytpconnect.rocket.common.RocketChatException
import ytpconnect.rocket.common.util.ifNull
import ytpconnect.rocket.core.RocketChatClient
import ytpconnect.rocket.core.internal.rest.resetAvatar
import ytpconnect.rocket.core.internal.rest.setAvatar
import ytpconnect.rocket.core.internal.rest.updateProfile
import java.util.*
import javax.inject.Inject

class ProfilePresenter @Inject constructor(
    private val view: ProfileView,
    private val strategy: CancelStrategy,
    private val uriInteractor: UriInteractor,
    val userHelper: UserHelper,
    serverInteractor: GetCurrentServerInteractor,
    factory: RocketChatClientFactory
) {
    private val serverUrl = serverInteractor.get()!!
    private val client: RocketChatClient = factory.create(serverUrl)
    private val myselfId = userHelper.user()?.id ?: ""
    private var myselfName = userHelper.user()?.name ?: ""
    private var myselfUsername = userHelper.username() ?: ""
    private var myselfEmailAddress = userHelper.user()?.emails?.getOrNull(0)?.address ?: ""

    fun loadUserProfile() {
        launchUI(strategy) {
            view.showLoading()
            try {
                view.showProfile(
                    serverUrl.avatarUrl(myselfUsername),
                    myselfName,
                    myselfUsername,
                    myselfEmailAddress
                )
            } catch (exception: RocketChatException) {
                view.showMessage(exception)
            } finally {
                view.hideLoading()
            }
        }
    }

    fun updateUserProfile(email: String, name: String, username: String) {
        launchUI(strategy) {
            view.showLoading()
            try {
                retryIO { client.updateProfile(myselfId, email, name, username) }

                myselfEmailAddress = email
                myselfName = name
                myselfUsername = username

                view.showProfileUpdateSuccessfullyMessage()
                loadUserProfile()
            } catch (exception: RocketChatException) {
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            } finally {
                view.hideLoading()
            }
        }
    }

    fun updateAvatar(uri: Uri) {
        launchUI(strategy) {
            view.showLoading()
            try {
                retryIO {
                    client.setAvatar(
                        uriInteractor.getFileName(uri) ?: uri.toString(),
                        uriInteractor.getMimeType(uri)
                    ) {
                        uriInteractor.getInputStream(uri)
                    }
                }
                view.reloadUserAvatar(serverUrl.avatarUrl(myselfUsername))
            } catch (exception: RocketChatException) {
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            } finally {
                view.hideLoading()
            }
        }
    }

    fun preparePhotoAndUpdateAvatar(bitmap: Bitmap) {
        launchUI(strategy) {
            view.showLoading()
            try {
                val byteArray = bitmap.compressImageAndGetByteArray("image/png")

                retryIO {
                    client.setAvatar(
                        UUID.randomUUID().toString() + ".png",
                        "image/png"
                    ) {
                        byteArray?.inputStream()
                    }
                }
                view.reloadUserAvatar(serverUrl.avatarUrl(myselfUsername))
            } catch (exception: RocketChatException) {
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            } finally {
                view.hideLoading()
            }
        }
    }

    fun resetAvatar() {
        launchUI(strategy) {
            view.showLoading()
            try {
                retryIO { client.resetAvatar(myselfId) }
                view.reloadUserAvatar(serverUrl.avatarUrl(myselfUsername))
            } catch (exception: RocketChatException) {
                exception.message?.let {
                    view.showMessage(it)
                }.ifNull {
                    view.showGenericErrorMessage()
                }
            } finally {
                view.hideLoading()
            }
        }
    }
}