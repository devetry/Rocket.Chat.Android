package chat.rocket.android.push

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
<<<<<<< HEAD
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.RemoteInput
import android.text.Html
import android.text.Spanned
import android.util.Log
import chat.rocket.android.BackgroundLooper
import chat.rocket.android.BuildConfig
import chat.rocket.android.R
import chat.rocket.android.RocketChatCache
import chat.rocket.android.activity.MainActivity
import chat.rocket.android.helper.Logger
import chat.rocket.core.interactors.MessageInteractor
import chat.rocket.core.models.Room
import chat.rocket.core.models.User
import chat.rocket.persistence.realm.repositories.RealmMessageRepository
import chat.rocket.persistence.realm.repositories.RealmRoomRepository
import chat.rocket.persistence.realm.repositories.RealmUserRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import okhttp3.HttpUrl
import org.json.JSONObject
import java.io.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap

typealias TupleRoomUser = Pair<Room, User>
typealias TupleGroupIdMessageCount = Pair<Int, AtomicInteger>

object PushManager {
    const val EXTRA_NOT_ID = "chat.rocket.android.EXTRA_NOT_ID"
    const val EXTRA_HOSTNAME = "chat.rocket.android.EXTRA_HOSTNAME"
    const val EXTRA_PUSH_MESSAGE = "chat.rocket.android.EXTRA_PUSH_MESSAGE"
    const val EXTRA_ROOM_ID = "chat.rocket.android.EXTRA_ROOM_ID"
    private const val REPLY_LABEL = "REPLY"
    private const val REMOTE_INPUT_REPLY = "REMOTE_INPUT_REPLY"

    // Notifications received from the same server are grouped in a single bundled notification.
    // This map associates a host to a group id.
    private val groupMap = HashMap<String, TupleGroupIdMessageCount>()

    // Map a hostname to a list of push messages that pertain to it.
    private val hostToPushMessageList = HashMap<String, MutableList<PushMessage>>()
    private val randomizer = Random()
=======
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.Html
import android.text.Spanned
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import chat.rocket.android.R
import chat.rocket.android.main.ui.MainActivity
import chat.rocket.android.server.domain.GetAccountInteractor
import chat.rocket.android.server.domain.GetSettingsInteractor
import chat.rocket.android.server.domain.siteName
import chat.rocket.android.server.ui.changeServerIntent
import chat.rocket.common.model.RoomType
import chat.rocket.common.model.roomTypeOf
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import se.ansman.kotshi.JsonSerializable
import se.ansman.kotshi.KotshiConstructor
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class PushManager @Inject constructor(
    private val groupedPushes: GroupedPush,
    private val manager: NotificationManager,
    private val moshi: Moshi,
    private val getAccountInteractor: GetAccountInteractor,
    private val getSettingsInteractor: GetSettingsInteractor,
    private val context: Context
) {

    private val random = Random()
>>>>>>> develop

    /**
     * Handles a receiving push by creating and displaying an appropriate notification based
     * on the *data* param bundle received.
     */
    @Synchronized
<<<<<<< HEAD
    fun handle(context: Context, data: Bundle) {
        val appContext = context.applicationContext
        val message = data["message"] as String?
        val image = data["image"] as String?
        val ejson = data["ejson"] as String?
        val notId = data["notId"] as String? ?: randomizer.nextInt().toString()
        val style = data["style"] as String?
        val summaryText = data["summaryText"] as String?
        val count = data["count"] as String?
        val title = data["title"] as String?

        if (ejson == null || message == null || title == null) {
            return
        }

        val lastPushMessage = PushMessage(title, message, image, ejson, count, notId, summaryText, style)

        // We should use Timber here
        if (BuildConfig.DEBUG) {
            Log.d(PushMessage::class.java.simpleName, lastPushMessage.toString())
        }

        showNotification(appContext, lastPushMessage)
    }

    /**
     * Clear all messages received to a given host the user is signed-in.
     */
    fun clearNotificationsByHost(host: String) {
        hostToPushMessageList.remove(host)
    }

    /**
     * Remove a notification solely by it's unique id.
     */
    fun clearNotificationsByNotificationId(notificationId: Int) {
        if (hostToPushMessageList.isNotEmpty()) {
            for (entry in hostToPushMessageList.entries) {
                entry.value.removeAll {
                    it.notificationId.toInt() == notificationId
                }
            }
        }
    }

    /**
     * Clear notifications by the host they belong to and its unique id.
     */
    fun clearNotificationsByHostAndNotificationId(host: String?, notificationId: Int?) {
        if (host == null || notificationId == null) {
            return
        }
        if (hostToPushMessageList.isNotEmpty()) {
            val notifications = hostToPushMessageList[host]
            notifications?.let {
                notifications.removeAll {
                    it.notificationId.toInt() == notificationId
                }
            }
        }
    }

    private fun isAndroidVersionAtLeast(minVersion: Int) = Build.VERSION.SDK_INT >= minVersion

    private fun getGroupForHost(host: String): TupleGroupIdMessageCount {
        val size = groupMap.size
        var group = groupMap.get(host)
        if (group == null) {
            group = TupleGroupIdMessageCount(size + 1, AtomicInteger(0))
            groupMap.put(host, group)
        }
        return group
    }

    @SuppressLint("NewApi")
    internal fun showNotification(context: Context, lastPushMessage: PushMessage) {
        if (lastPushMessage.host == null || lastPushMessage.message == null || lastPushMessage.title == null) {
            return
        }
        val manager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notId = lastPushMessage.notificationId.toInt()
        val host = lastPushMessage.host
        val groupTuple = getGroupForHost(host)

        groupTuple.second.incrementAndGet()
        val notIdListForHostname: MutableList<PushMessage>? = hostToPushMessageList.get(host)
        if (notIdListForHostname == null) {
            hostToPushMessageList.put(host, arrayListOf(lastPushMessage))
        } else {
            notIdListForHostname.add(0, lastPushMessage)
        }
        if (isAndroidVersionAtLeast(Build.VERSION_CODES.N)) {
            val notification = createSingleNotificationForNougatAndAbove(context, lastPushMessage)
            val groupNotification = createGroupNotificationForNougatAndAbove(context, lastPushMessage)
            notification?.let {
                manager.notify(notId, notification)
            }

            groupNotification?.let {
                manager.notify(groupTuple.first, groupNotification)
            }
        } else {
            val notification = createSingleNotification(context, lastPushMessage)
            val pushMessageList = hostToPushMessageList.get(host)

            notification?.let {
                NotificationManagerCompat.from(context).notify(notId, notification)
            }

            pushMessageList?.let {
                if (pushMessageList.size > 1) {
                    val groupNotification = createGroupNotification(context, lastPushMessage)
                    groupNotification?.let {
                        NotificationManagerCompat.from(context).notify(groupTuple.first, groupNotification)
                    }
=======
    fun handle(data: Bundle) = runBlocking {
        val message = data["message"] as String?
        val ejson = data["ejson"] as String?
        val title = data["title"] as String?
        val notId = data["notId"] as String? ?: random.nextInt().toString()
        val image = data["image"] as String?
        val style = data["style"] as String?
        val summaryText = data["summaryText"] as String?
        val count = data["count"] as String?

        try {
            val adapter = moshi.adapter<PushInfo>(PushInfo::class.java)

            val pushMessage = if (ejson != null) {
                val info = adapter.fromJson(ejson)
                PushMessage(title!!, message!!, info!!, image, count, notId, summaryText, style)
            } else {
                PushMessage(title!!, message!!, PushInfo.EMPTY, image, count, notId, summaryText, style)
            }

            Timber.d("Received push message: $pushMessage")

            showNotification(pushMessage)
        } catch (ex: Exception) {
            Timber.e(ex, "Error parsing PUSH message: $data")
            ex.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    suspend fun showNotification(pushMessage: PushMessage) {
        val notId = pushMessage.notificationId.toInt()
        val host = pushMessage.info.host

        if (!hasAccount(host)) {
            createSingleNotification(pushMessage)?.let {
                NotificationManagerCompat.from(context).notify(notId, it)
            }
            Timber.d("ignoring push message: $pushMessage (maybe a test notification?)")
            return
        }

        val groupTuple = getGroupForHost(host)

        groupTuple.second.incrementAndGet()
        val notIdListForHostname: MutableList<PushMessage>? = groupedPushes.hostToPushMessageList[host]
        if (notIdListForHostname == null) {
            groupedPushes.hostToPushMessageList[host] = arrayListOf(pushMessage)
        } else {
            notIdListForHostname.add(0, pushMessage)
        }

        val notification = createSingleNotification(pushMessage)
        val pushMessageList = groupedPushes.hostToPushMessageList[host]

        notification?.let {
            manager.notify(notId, it)
        }

        pushMessageList?.let {
            if (pushMessageList.size > 1) {
                val groupNotification = createGroupNotification(pushMessage)
                groupNotification?.let {
                    NotificationManagerCompat.from(context).notify(groupTuple.first, groupNotification)
>>>>>>> develop
                }
            }
        }
    }

<<<<<<< HEAD
    internal fun createGroupNotification(context: Context, lastPushMessage: PushMessage): Notification? {
        with(lastPushMessage) {
            if (host == null || message == null || title == null) {
                return null
            }
            val id = lastPushMessage.notificationId.toInt()
            val contentIntent = getContentIntent(context, id, lastPushMessage)
            val deleteIntent = getDismissIntent(context, lastPushMessage)
            val builder = NotificationCompat.Builder(context)
                    .setWhen(createdAt)
                    .setContentTitle(title.fromHtml())
                    .setContentText(message.fromHtml())
                    .setGroup(host)
                    .setGroupSummary(true)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(deleteIntent)
                    .setMessageNotification()

            val subText = RocketChatCache.getHostSiteName(host)
            if (subText.isNotEmpty()) {
                builder.setSubText(subText)
            }

            if (style == null || style == "inbox") {
                val pushMessageList = hostToPushMessageList.get(host)

                pushMessageList?.let {
                    val messageCount = pushMessageList.size
                    val summary = summaryText?.replace("%n%", messageCount.toString())
                            ?.fromHtml() ?: "$messageCount new messages"
                    builder.setNumber(messageCount)
                    if (messageCount > 1) {
                        val firstPush = pushMessageList[0]
                        val singleConversation = pushMessageList.filter {
                            firstPush.sender?.username != it.sender?.username
                        }.isEmpty()

                        val inbox = NotificationCompat.InboxStyle()
                                .setBigContentTitle(if (singleConversation) title else summary)

                        for (push in pushMessageList) {
                            if (singleConversation) {
                                inbox.addLine(push.message)
                            } else {
                                inbox.addLine("<font color='black'>${push.title}</font> <font color='gray'>${push.message}</font>".fromHtml())
                            }
                        }

                        builder.setStyle(inbox)
                    } else {
                        val firstMsg = pushMessageList[0]
                        if (firstMsg.host == null || firstMsg.message == null || firstMsg.title == null) {
                            return null
                        }
                        val bigText = NotificationCompat.BigTextStyle()
                                .bigText(firstMsg.message.fromHtml())
                                .setBigContentTitle(firstMsg.title.fromHtml())

                        builder.setStyle(bigText)
                    }
                }
            } else {
                val bigText = NotificationCompat.BigTextStyle()
                        .bigText(message.fromHtml())
                        .setBigContentTitle(title.fromHtml())

                builder.setStyle(bigText)
            }

            return builder.build()
        }
=======
    private fun getGroupForHost(host: String): TupleGroupIdMessageCount {
        val size = groupedPushes.groupMap.size
        var group = groupedPushes.groupMap[host]
        if (group == null) {
            group = TupleGroupIdMessageCount(size + 1, AtomicInteger(0))
            groupedPushes.groupMap[host] = group
        }
        return group
    }

    private suspend fun hasAccount(host: String): Boolean {
        return getAccountInteractor.get(host) != null
>>>>>>> develop
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
<<<<<<< HEAD
    internal fun createGroupNotificationForNougatAndAbove(context: Context, lastPushMessage: PushMessage): Notification? {
        with(lastPushMessage) {
            if (host == null || message == null || title == null) {
                return null
            }
            val manager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = notificationId.toInt()
            val contentIntent = getContentIntent(context, id, lastPushMessage, grouped = true)
            val deleteIntent = getDismissIntent(context, lastPushMessage)

            val builder = Notification.Builder(context)
                    .setWhen(createdAt)
                    .setContentTitle(title.fromHtml())
                    .setContentText(message.fromHtml())
                    .setGroup(host)
                    .setGroupSummary(true)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(deleteIntent)
                    .setMessageNotification(context)

            if (isAndroidVersionAtLeast(Build.VERSION_CODES.O)) {
                builder.setChannelId(host)
                val groupChannel = NotificationChannel(host, host, NotificationManager.IMPORTANCE_HIGH)
                groupChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                groupChannel.enableLights(false)
                groupChannel.enableVibration(true)
                groupChannel.setShowBadge(true)
                manager.createNotificationChannel(groupChannel)
            }

            val subText = RocketChatCache.getHostSiteName(host)
            if (subText.isNotEmpty()) {
                builder.setSubText(subText)
            }

            if (style == null || style == "inbox") {
                val pushMessageList = hostToPushMessageList.get(host)
=======
    private fun createGroupNotification(pushMessage: PushMessage): Notification? {
        with(pushMessage) {
            val host = info.host

            val builder = createBaseNotificationBuilder(pushMessage, grouped = true)
                .setGroupSummary(true)

            if (style == null || style == "inbox") {
                val pushMessageList = groupedPushes.hostToPushMessageList[host]
>>>>>>> develop

                pushMessageList?.let {
                    val count = pushMessageList.filter {
                        it.title == title
                    }.size

                    builder.setContentTitle(getTitle(count, title))

<<<<<<< HEAD
                    val inbox = Notification.InboxStyle()
                            .setBigContentTitle(getTitle(count, title))
=======
                    val inbox = NotificationCompat.InboxStyle()
                        .setBigContentTitle(getTitle(count, title))
>>>>>>> develop

                    for (push in pushMessageList) {
                        inbox.addLine(push.message)
                    }

                    builder.setStyle(inbox)
                }
            } else {
<<<<<<< HEAD
                val bigText = Notification.BigTextStyle()
                        .bigText(message.fromHtml())
                        .setBigContentTitle(title.fromHtml())
=======
                val bigText = NotificationCompat.BigTextStyle()
                    .bigText(message.fromHtml())
                    .setBigContentTitle(title.fromHtml())
>>>>>>> develop

                builder.setStyle(bigText)
            }

            return builder.build()
        }
    }

<<<<<<< HEAD
    internal fun createSingleNotification(context: Context, lastPushMessage: PushMessage): Notification? {
        with(lastPushMessage) {
            if (host == null || message == null || title == null) {
                return null
            }
            val id = notificationId.toInt()
            val contentIntent = getContentIntent(context, id, lastPushMessage)
            val deleteIntent = getDismissIntent(context, lastPushMessage)

            val builder = NotificationCompat.Builder(context)
                    .setWhen(createdAt)
                    .setContentTitle(title.fromHtml())
                    .setContentText(message.fromHtml())
                    .setGroupSummary(false)
                    .setGroup(host)
                    .setDeleteIntent(deleteIntent)
                    .setContentIntent(contentIntent)
                    .setMessageNotification()

            val subText = RocketChatCache.getHostSiteName(host)
            if (subText.isNotEmpty()) {
                builder.setSubText(subText)
            }

            val pushMessageList = hostToPushMessageList.get(host)

            pushMessageList?.let {
                val lastPushMsg = pushMessageList.last()
                if (lastPushMsg.host == null || lastPushMsg.message == null || lastPushMsg.title == null) {
                    return null
                }
                if (pushMessageList.isNotEmpty()) {
                    val messageCount = pushMessageList.size

                    val bigText = NotificationCompat.BigTextStyle()
                            .bigText(lastPushMsg.message.fromHtml())
                            .setBigContentTitle(lastPushMsg.title.fromHtml())
                    builder.setStyle(bigText).setNumber(messageCount)
                }
            }

            return builder.build()
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    internal fun createSingleNotificationForNougatAndAbove(context: Context, lastPushMessage: PushMessage): Notification? {
        val manager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(lastPushMessage) {
            if (host == null || message == null || title == null) {
                return null
            }
            val id = notificationId.toInt()
            val contentIntent = getContentIntent(context, id, lastPushMessage)
            val deleteIntent = getDismissIntent(context, lastPushMessage)

            val builder = Notification.Builder(context)
                    .setWhen(createdAt)
                    .setContentTitle(title.fromHtml())
                    .setContentText(message.fromHtml())
                    .setGroup(host)
                    .setGroupSummary(false)
                    .setDeleteIntent(deleteIntent)
                    .setContentIntent(contentIntent)
                    .setMessageNotification(context)
                    .addReplyAction(context, lastPushMessage)

            if (isAndroidVersionAtLeast(android.os.Build.VERSION_CODES.O)) {
                builder.setChannelId(host)
                val channel = NotificationChannel(host, host, NotificationManager.IMPORTANCE_HIGH)
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                channel.enableLights(false)
                channel.enableVibration(true)
                channel.setShowBadge(true)
                manager.createNotificationChannel(channel)
            }

            val subText = RocketChatCache.getHostSiteName(host)
            if (subText.isNotEmpty()) {
                builder.setSubText(subText)
            }

            if (style == null || "inbox" == style) {
                val pushMessageList = hostToPushMessageList.get(host)

                pushMessageList?.let {
                    val userMessages = pushMessageList.filter {
                        it.notificationId == lastPushMessage.notificationId
=======
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createSingleNotification(pushMessage: PushMessage): Notification? {
        with(pushMessage) {
            val host = info.host

            val builder = createBaseNotificationBuilder(pushMessage)
                .setGroupSummary(false)

            if (style == null || "inbox" == style) {
                val pushMessageList = groupedPushes.hostToPushMessageList.get(host)

                if (pushMessageList != null) {
                    val userMessages = pushMessageList.filter {
                        it.notificationId == pushMessage.notificationId
>>>>>>> develop
                    }

                    val count = pushMessageList.filter {
                        it.title == title
                    }.size

                    builder.setContentTitle(getTitle(count, title))

                    if (count > 1) {
<<<<<<< HEAD
                        val inbox = Notification.InboxStyle()
=======
                        val inbox = NotificationCompat.InboxStyle()
>>>>>>> develop
                        inbox.setBigContentTitle(getTitle(count, title))
                        for (push in userMessages) {
                            inbox.addLine(push.message)
                        }

                        builder.setStyle(inbox)
                    } else {
<<<<<<< HEAD
                        val bigTextStyle = Notification.BigTextStyle()
                                .bigText(message.fromHtml())
                        builder.setStyle(bigTextStyle)
                    }
                }
            } else {
                val bigTextStyle = Notification.BigTextStyle()
                        .bigText(message.fromHtml())
                builder.setStyle(bigTextStyle)
            }

            return builder.build()
        }
    }

=======
                        val bigTextStyle = NotificationCompat.BigTextStyle()
                            .bigText(message.fromHtml())
                        builder.setStyle(bigTextStyle)
                    }
                } else {
                    // We don't know which kind of push is this - maybe a test push, so just show it
                    val bigTextStyle = NotificationCompat.BigTextStyle()
                        .bigText(message.fromHtml())
                    builder.setStyle(bigTextStyle)
                    return builder.build()
                }
            } else {
                val bigTextStyle = NotificationCompat.BigTextStyle()
                    .bigText(message.fromHtml())
                builder.setStyle(bigTextStyle)
            }

            return builder.addReplyAction(pushMessage).build()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBaseNotificationBuilder(pushMessage: PushMessage, grouped: Boolean = false): NotificationCompat.Builder {
        return with(pushMessage) {
            val id = notificationId.toInt()
            val host = info.host
            val contentIntent = getContentIntent(context, id, pushMessage, grouped)
            val deleteIntent = getDismissIntent(context, pushMessage)

            val builder = NotificationCompat.Builder(context, host)
                .setWhen(info.createdAt)
                .setContentTitle(title.fromHtml())
                .setContentText(message.fromHtml())
                .setGroup(host)
                .setDeleteIntent(deleteIntent)
                .setContentIntent(contentIntent)
                .setMessageNotification()

            if (host.isEmpty()) {
                builder.setContentIntent(deleteIntent)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId: String
                val channelName: String
                if (host.isEmpty()) {
                    channelName = "Test Notification"
                    channelId = "test-channel"
                } else {
                    channelName = host
                    channelId = host
                }
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                channel.enableLights(false)
                channel.enableVibration(true)
                channel.setShowBadge(true)
                manager.createNotificationChannel(channel)
                builder.setChannelId(channelId)
            }

            //TODO: Get Site_Name PublicSetting from cache
            val subText = getSiteName(host)
            if (subText.isNotEmpty()) {
                builder.setSubText(subText)
            }

            return@with builder
        }
    }

    private fun getSiteName(host: String): String {
        val settings = getSettingsInteractor.get(host)
        return settings.siteName() ?: "YTP"
    }

>>>>>>> develop
    private fun getTitle(messageCount: Int, title: String): CharSequence {
        return if (messageCount > 1) "($messageCount) ${title.fromHtml()}" else title.fromHtml()
    }

    private fun getDismissIntent(context: Context, pushMessage: PushMessage): PendingIntent {
        val deleteIntent = Intent(context, DeleteReceiver::class.java)
<<<<<<< HEAD
                .putExtra(EXTRA_NOT_ID, pushMessage.notificationId.toInt())
                .putExtra(EXTRA_HOSTNAME, pushMessage.host)
=======
            .putExtra(EXTRA_NOT_ID, pushMessage.notificationId.toInt())
            .putExtra(EXTRA_HOSTNAME, pushMessage.info.host)
>>>>>>> develop
        return PendingIntent.getBroadcast(context, pushMessage.notificationId.toInt(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getContentIntent(context: Context, notificationId: Int, pushMessage: PushMessage, grouped: Boolean = false): PendingIntent {
<<<<<<< HEAD
        val notificationIntent = Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_NOT_ID, notificationId)
                .putExtra(EXTRA_HOSTNAME, pushMessage.host)
        if (!grouped) {
            notificationIntent.putExtra(EXTRA_ROOM_ID, pushMessage.rid)
        }
        return PendingIntent.getActivity(context, randomizer.nextInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
=======
        val roomId = if (!grouped) pushMessage.info.roomId else null
        val notificationIntent = context.changeServerIntent(pushMessage.info.host, chatRoomId = roomId)
        return PendingIntent.getActivity(context, random.nextInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
>>>>>>> develop
    }

    // CharSequence extensions
    private fun CharSequence.fromHtml(): Spanned {
<<<<<<< HEAD
        return Html.fromHtml(this as String)
    }

    //Notification.Builder extensions
    @RequiresApi(Build.VERSION_CODES.N)
    private fun Notification.Builder.addReplyAction(ctx: Context, pushMessage: PushMessage): Notification.Builder {
        val replyRemoteInput = android.app.RemoteInput.Builder(REMOTE_INPUT_REPLY)
                .setLabel(REPLY_LABEL)
                .build()
        val replyIntent = Intent(ctx, ReplyReceiver::class.java)
        replyIntent.putExtra(EXTRA_PUSH_MESSAGE, pushMessage as Serializable)
        val pendingIntent = PendingIntent.getBroadcast(
                ctx, randomizer.nextInt(), replyIntent, 0)
        val replyAction =
                Notification.Action.Builder(
                        Icon.createWithResource(ctx, R.drawable.ic_reply), REPLY_LABEL, pendingIntent)
                        .addRemoteInput(replyRemoteInput)
                        .setAllowGeneratedReplies(true)
                        .build()
        this.addAction(replyAction)
        return this
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun Notification.Builder.setMessageNotification(ctx: Context): Notification.Builder {
        val res = ctx.resources
        val smallIcon = res.getIdentifier(
                "rocket_chat_notification", "drawable", ctx.packageName)
        with(this, {
            setAutoCancel(true)
            setShowWhen(true)
            setColor(res.getColor(R.color.colorRed400, ctx.theme))
            setDefaults(Notification.DEFAULT_ALL)
            setSmallIcon(smallIcon)
        })
        return this
=======
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this as String, FROM_HTML_MODE_LEGACY, null, null)
        } else {
            Html.fromHtml(this as String)
        }
>>>>>>> develop
    }

    // NotificationCompat.Builder extensions
    private fun NotificationCompat.Builder.addReplyAction(pushMessage: PushMessage): NotificationCompat.Builder {
<<<<<<< HEAD
        val context = this.mContext
        val replyRemoteInput = RemoteInput.Builder(REMOTE_INPUT_REPLY)
                .setLabel(REPLY_LABEL)
                .build()
        val replyIntent = Intent(context, ReplyReceiver::class.java)
        replyIntent.putExtra(EXTRA_PUSH_MESSAGE, pushMessage as Serializable)
        val pendingIntent = PendingIntent.getBroadcast(
                context, randomizer.nextInt(), replyIntent, 0)
        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_reply, REPLY_LABEL, pendingIntent)
                .addRemoteInput(replyRemoteInput)
                .setAllowGeneratedReplies(true)
                .build()
=======
        val replyTextHint = context.getText(R.string.notif_action_reply_hint)
        val replyRemoteInput = RemoteInput.Builder(REMOTE_INPUT_REPLY)
            .setLabel(replyTextHint)
            .build()
        val pendingIntent = getReplyPendingIntent(pushMessage)
        val replyAction = NotificationCompat.Action.Builder(R.drawable.ic_action_message_reply_24dp, replyTextHint, pendingIntent)
            .addRemoteInput(replyRemoteInput)
            .setAllowGeneratedReplies(true)
            .build()
>>>>>>> develop

        this.addAction(replyAction)
        return this
    }

<<<<<<< HEAD
    private fun NotificationCompat.Builder.setMessageNotification(): NotificationCompat.Builder {
        val ctx = this.mContext
        val res = ctx.resources
        val smallIcon = res.getIdentifier(
                "rocket_chat_notification", "drawable", ctx.packageName)
        with(this, {
            setAutoCancel(true)
            setShowWhen(true)
            color = ctx.resources.getColor(R.color.colorRed400)
            setDefaults(Notification.DEFAULT_ALL)
            setSmallIcon(smallIcon)
        })
        return this
    }

    internal data class PushMessage(
            val title: String? = null,
            val message: String? = null,
            val image: String? = null,
            val ejson: String? = null,
            val count: String? = null,
            val notificationId: String,
            val summaryText: String? = null,
            val style: String? = null) : Serializable {
        val host: String?
        val rid: String?
        val type: String?
        val channelName: String?
        val sender: Sender?
        val createdAt: Long

        init {
            val json = if (ejson == null) JSONObject() else JSONObject(ejson)
            host = json.optString("host", null)
            rid = json.optString("rid", null)
            type = json.optString("type", null)
            channelName = json.optString("name", null)
            val senderJson = json.optString("sender", null)
            if (senderJson != null && senderJson != "null") {
                sender = Sender(senderJson)
            } else {
                sender = null
            }
            createdAt = System.currentTimeMillis()
        }

        data class Sender(val sender: String) : Serializable {
            val _id: String?
            val username: String?
            val name: String?

            init {
                val json = JSONObject(sender)
                _id = json.optString("_id", null)
                username = json.optString("username", null)
                name = json.optString("name", null)
            }
        }
    }

    /**
     * BroadcastReceiver for dismissed notifications.
     */
    class DeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val notId = intent?.extras?.getInt(EXTRA_NOT_ID)
            val host = intent?.extras?.getString(EXTRA_HOSTNAME)
            if (host != null && notId != null) {
                clearNotificationsByHostAndNotificationId(host, notId)
            }
        }
    }

    /**
     * *EXPERIMENTAL*
     *
     * BroadcastReceiver for notifications' replies using Direct Reply feature (Android >= 7).
     */
    class ReplyReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null) {
                return
            }

            synchronized(this) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val message: CharSequence? = extractMessage(intent)
                val pushMessage = intent?.extras?.getSerializable(EXTRA_PUSH_MESSAGE) as PushMessage?

                if (pushMessage?.host == null) {
                    return
                }

                pushMessage.let {
                    val groupTuple = groupMap.get(pushMessage.host)
                    val pushes = hostToPushMessageList.get(pushMessage.host)
                    pushes?.let {
                        val allMessagesFromSameUser = pushes.filter {
                            it.sender?._id == pushMessage.sender?._id
                        }
                        for (msg in allMessagesFromSameUser) {
                            manager.cancel(msg.notificationId.toInt())
                            groupTuple?.second?.decrementAndGet()
                        }

                        groupTuple?.let {
                            val groupNotId = groupTuple.first
                            val totalNot = groupTuple.second.get()
                            if (totalNot == 0) {
                                manager.cancel(groupNotId)
                            }
                        }
                        message?.let {
                            if (pushMessage.rid == null) {
                                return
                            }
                            val httpUrl = HttpUrl.parse(pushMessage.host)
                            httpUrl?.let {
                                val siteUrl = RocketChatCache.getSiteUrlFor(httpUrl.host())
                                if (siteUrl != null) {
                                    sendMessage(siteUrl, message, pushMessage.rid)
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun extractMessage(intent: Intent?): CharSequence? {
            val remoteInput: Bundle? =
                    RemoteInput.getResultsFromIntent(intent)
            return remoteInput?.getCharSequence(REMOTE_INPUT_REPLY)
        }

        // Just kept for reference. We should use this on rewrite with job schedulers
        private fun sendMessage(hostname: String, message: CharSequence, roomId: String) {
            val roomRepository = RealmRoomRepository(hostname)
            val userRepository = RealmUserRepository(hostname)
            val messageRepository = RealmMessageRepository(hostname)
            val messageInteractor = MessageInteractor(messageRepository, roomRepository)

            val singleRoom: Single<Room> = roomRepository.getById(roomId)
                    .filter({ it.isPresent })
                    .map({ it.get() })
                    .firstElement()
                    .toSingle()

            val singleUser: Single<User> = userRepository.getCurrent()
                    .filter({ it.isPresent })
                    .map({ it.get() })
                    .firstElement()
                    .toSingle()

            val roomUserTuple: Single<TupleRoomUser> = Single.zip(
                    singleRoom,
                    singleUser,
                    BiFunction { room, user -> TupleRoomUser(room, user) })

            roomUserTuple.flatMap { tuple -> messageInteractor.send(tuple.first, tuple.second, message as String) }
                    .subscribeOn(AndroidSchedulers.from(BackgroundLooper.get()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ ->
                        // Empty
                    }, { throwable ->
                        Logger.report(throwable)
                    })
        }
    }
}
=======
    private fun getReplyIntent(pushMessage: PushMessage): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent(context, DirectReplyReceiver::class.java)
        } else {
            Intent(context, MainActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }.also {
            it.action = ACTION_REPLY
            it.putExtra(EXTRA_PUSH_MESSAGE, pushMessage)
        }
    }

    private fun getReplyPendingIntent(pushMessage: PushMessage): PendingIntent {
        val replyIntent = getReplyIntent(pushMessage)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PendingIntent.getBroadcast(
                context,
                random.nextInt(),
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                context,
                random.nextInt(),
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    private fun NotificationCompat.Builder.setMessageNotification(): NotificationCompat.Builder {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val res = context.resources
//        val smallIcon = res.getIdentifier(
//            "rocket_chat_notification", "drawable", context.packageName)
        with(this) {
            setAutoCancel(true)
            setShowWhen(true)
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            setDefaults(Notification.DEFAULT_ALL)
            setSmallIcon(R.mipmap.ic_launcher)
            setSound(alarmSound)
        }
        return this
    }
}

data class PushMessage(
    val title: String,
    val message: String,
    val info: PushInfo,
    val image: String? = null,
    val count: String? = null,
    val notificationId: String,
    val summaryText: String? = null,
    val style: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readParcelable(PushMessage::class.java.classLoader) ?: PushInfo.EMPTY,
        parcel.readString(),
        parcel.readString(),
        parcel.readString().orEmpty(),
        parcel.readString(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeParcelable(info, flags)
        parcel.writeString(image)
        parcel.writeString(count)
        parcel.writeString(notificationId)
        parcel.writeString(summaryText)
        parcel.writeString(style)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushMessage> {
        override fun createFromParcel(parcel: Parcel): PushMessage {
            return PushMessage(parcel)
        }

        override fun newArray(size: Int): Array<PushMessage?> {
            return arrayOfNulls(size)
        }
    }
}

@JsonSerializable
data class PushInfo @KotshiConstructor constructor(
    @Json(name = "host") val hostname: String,
    @Json(name = "rid") val roomId: String,
    val type: RoomType,
    val name: String?,
    val sender: PushSender?
) : Parcelable {
    val createdAt: Long
        get() = System.currentTimeMillis()
    val host by lazy {
        sanitizeUrl(hostname)
    }

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        roomTypeOf(parcel.readString().orEmpty()),
        parcel.readString(),
        parcel.readParcelable(PushInfo::class.java.classLoader))

    private fun sanitizeUrl(baseUrl: String): String {
        var url = baseUrl.trim()
        while (url.endsWith('/')) {
            url = url.dropLast(1)
        }

        return url
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(hostname)
        parcel.writeString(roomId)
        parcel.writeString(type.toString())
        parcel.writeString(name)
        parcel.writeParcelable(sender, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushInfo> {
        val EMPTY = PushInfo(hostname = "", roomId = "", type = roomTypeOf(RoomType.CHANNEL), name = "",
            sender = null)

        override fun createFromParcel(parcel: Parcel): PushInfo {
            return PushInfo(parcel)
        }

        override fun newArray(size: Int): Array<PushInfo?> {
            return arrayOfNulls(size)
        }
    }
}

@JsonSerializable
data class PushSender @KotshiConstructor constructor(
    @Json(name = "_id") val id: String,
    val username: String?,
    val name: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PushSender> {
        override fun createFromParcel(parcel: Parcel): PushSender {
            return PushSender(parcel)
        }

        override fun newArray(size: Int): Array<PushSender?> {
            return arrayOfNulls(size)
        }
    }
}

const val EXTRA_NOT_ID = "chat.rocket.android.EXTRA_NOT_ID"
const val EXTRA_HOSTNAME = "chat.rocket.android.EXTRA_HOSTNAME"
const val EXTRA_PUSH_MESSAGE = "chat.rocket.android.EXTRA_PUSH_MESSAGE"
const val EXTRA_ROOM_ID = "chat.rocket.android.EXTRA_ROOM_ID"
const val ACTION_REPLY = "chat.rocket.android.ACTION_REPLY"
const val REMOTE_INPUT_REPLY = "REMOTE_INPUT_REPLY"
>>>>>>> develop
