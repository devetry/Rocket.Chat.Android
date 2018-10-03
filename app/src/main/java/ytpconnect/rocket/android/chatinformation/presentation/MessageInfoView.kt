package ytpconnect.rocket.android.chatinformation.presentation

import ytpconnect.rocket.android.chatinformation.viewmodel.ReadReceiptViewModel
import ytpconnect.rocket.android.core.behaviours.LoadingView

interface MessageInfoView : LoadingView {

    fun showGenericErrorMessage()

    fun showReadReceipts(messageReceipts: List<ReadReceiptViewModel>)
}
