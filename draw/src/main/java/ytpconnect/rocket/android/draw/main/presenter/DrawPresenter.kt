package ytpconnect.rocket.android.draw.main.presenter

import android.graphics.Bitmap
import ytpconnect.rocket.android.core.lifecycle.CancelStrategy
import ytpconnect.rocket.android.util.extension.compressImageAndGetByteArray
import ytpconnect.rocket.android.util.extension.launchUI
import javax.inject.Inject

class DrawPresenter @Inject constructor(
    private val view: DrawView,
    private val strategy: CancelStrategy
) {

    fun processDrawingImage(bitmap: Bitmap) {
        launchUI(strategy) {
            val byteArray = bitmap.compressImageAndGetByteArray("image/png")
            if (byteArray != null) {
                view.sendByteArray(byteArray)
            } else {
                view.showWrongProcessingMessage()
            }
        }
    }
}