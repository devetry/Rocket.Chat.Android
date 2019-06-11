package chat.rocket.android.chatrooms.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import chat.rocket.android.chatrooms.adapter.ItemHolder
import chat.rocket.android.chatrooms.adapter.LoadingItemHolder
import chat.rocket.android.chatrooms.adapter.RoomUiModelMapper
import chat.rocket.android.chatrooms.adapter.model.RoomUiModel
import chat.rocket.android.chatrooms.domain.FetchChatRoomsInteractor
import chat.rocket.android.chatrooms.infrastructure.ChatRoomsRepository
import chat.rocket.android.server.infraestructure.ConnectionManager
import chat.rocket.android.util.livedata.transform
import chat.rocket.android.util.livedata.wrap
import chat.rocket.android.util.retryIO
import chat.rocket.common.util.ifNull
import chat.rocket.core.internal.realtime.socket.model.State
import chat.rocket.core.internal.rest.spotlight
import chat.rocket.core.model.SpotlightResult
import com.shopify.livedataktx.distinct
import com.shopify.livedataktx.map
import com.shopify.livedataktx.nonNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.IllegalArgumentException
import kotlin.coroutines.coroutineContext

class ChatRoomsViewModel(
    private val connectionManager: ConnectionManager,
    private val interactor: FetchChatRoomsInteractor,
    private val repository: ChatRoomsRepository,
    private val mapper: RoomUiModelMapper
) : ViewModel() {
    private val query = MutableLiveData<Query>()
    val loadingState = MutableLiveData<LoadingState>()
    private val runContext = newSingleThreadContext("chat-rooms-view-model")
    private val client = connectionManager.client
    private var loaded = false
    var showLastMessage = true

    fun getChatRooms(): LiveData<RoomsModel> {
        return Transformations.switchMap(query) { query ->

            return@switchMap if (query.isSearch()) {
                this@ChatRoomsViewModel.query.wrap(runContext) { _, data: MutableLiveData<RoomsModel> ->
                    val string = (query as Query.Search).query

                    // debounce, to not query while the user is writing
                    delay(200)
                    // TODO - find a better way for cancellation checking
                    if (!coroutineContext.isActive) return@wrap


                    var rooms = repository.search(string).let { mapper.map(it, showLastMessage = this.showLastMessage) }
                    /**
                     * ROCKET CHAT CODE EDITED
                     *
                     * DESCRIPTION: Searches for all channels when search is empty
                     *
                     * AUTHOR: Tanner Juby
                     * DATE: 6/11/19
                     *
                     * START OF EDIT
                     */
                    if (string == "") {
                        rooms = repository.search("a").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("b").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("c").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("d").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("e").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("f").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("g").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("h").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("i").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("j").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("k").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("l").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("m").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("n").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("o").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("p").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("q").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("r").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("s").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("t").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("u").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("v").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("w").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("x").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("y").let { mapper.map(it, showLastMessage = this.showLastMessage) } +
                                repository.search("z").let { mapper.map(it, showLastMessage = this.showLastMessage) }
                        rooms = rooms.distinctBy { (it.data as RoomUiModel).name }
                    }
                    /**
                     * END OF EDIT
                     *
                     * PREVIOUS CODE: n/a
                     */
                    data.postValue(rooms.toMutableList() + LoadingItemHolder())

                    if (!coroutineContext.isActive) return@wrap

                    val spotlight = spotlight(query.query)?.let { mapper.map(it, showLastMessage = this.showLastMessage) }
                    if (!coroutineContext.isActive) return@wrap

                    spotlight?.let {
                        data.postValue(rooms.toMutableList() + spotlight)
                    }.ifNull {
                        data.postValue(rooms)
                    }
                }
            } else {
                repository.getChatRooms(query.asSortingOrder())
                        .nonNull()
                        .distinct()
                        .transform(runContext) { rooms ->
                            val mappedRooms = rooms?.let {
                                mapper.map(rooms, query.isGrouped(), this.showLastMessage)
                            }
                            if (loaded && mappedRooms?.isEmpty() == true) {
                                loadingState.postValue(LoadingState.Loaded(0))
                            }
                            mappedRooms
                        }
            }
        }
    }

    private suspend fun spotlight(query: String): SpotlightResult? {
        return try {
            retryIO { client.spotlight(query) }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun getStatus(): MutableLiveData<State> {
        return connectionManager.statusLiveData.nonNull().distinct().map { state ->
            if (state is State.Connected) {
                fetchRooms()
            }
            state
        }
    }

    private fun fetchRooms() {
        GlobalScope.launch {
            setLoadingState(LoadingState.Loading(repository.count()))
            try {
                interactor.refreshChatRooms()
                setLoadingState(LoadingState.Loaded(repository.count()))
                loaded = true
            } catch (ex: Exception) {
                Timber.d(ex, "Error refreshing chatrooms")
                setLoadingState(LoadingState.Error(repository.count()))
            }
        }
    }

    fun setQuery(query: Query) {
        this.query.value = query
    }

    private suspend fun setLoadingState(state: LoadingState) {
        withContext(Dispatchers.Main) {
            loadingState.value = state
        }
    }
}

typealias RoomsModel = List<ItemHolder<*>>

sealed class LoadingState {
    data class Loading(val count: Long) : LoadingState()
    data class Loaded(val count: Long) : LoadingState()
    data class Error(val count: Long) : LoadingState()
}

sealed class Query {
    data class ByActivity(val grouped: Boolean = false) : Query()
    data class ByName(val grouped: Boolean = false) : Query()
    data class Search(val query: String) : Query()
}

fun Query.isSearch(): Boolean = this is Query.Search

fun Query.isGrouped(): Boolean {
    return when(this) {
        is Query.Search -> false
        is Query.ByName -> grouped
        is Query.ByActivity -> grouped
    }
}

fun Query.asSortingOrder(): ChatRoomsRepository.Order {
    return when(this) {
        is Query.ByName -> {
            if (grouped) {
                ChatRoomsRepository.Order.GROUPED_NAME
            } else {
                ChatRoomsRepository.Order.NAME
            }
        }
        is Query.ByActivity -> {
            if (grouped) {
                ChatRoomsRepository.Order.GROUPED_ACTIVITY
            } else {
                ChatRoomsRepository.Order.ACTIVITY
            }
        }
        else -> throw IllegalArgumentException("Should be ByName or ByActivity")
    }
}
