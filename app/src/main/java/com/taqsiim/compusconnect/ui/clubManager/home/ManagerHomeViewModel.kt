package com.taqsiim.compusconnect.ui.clubManager.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.CreatePostRequest
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.data.repository.PostRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerHomeState(
    val posts: UiState<List<Post>> = UiState.Loading,
    val events: UiState<List<Event>> = UiState.Loading,
    val isRefreshing: Boolean = false
)

sealed class ManagerHomeIntent {
    data object LoadPosts : ManagerHomeIntent()
    data object LoadEvents : ManagerHomeIntent()
    data class CreatePost(val content: String, val eventId: Int?, val imageUrl: String?) : ManagerHomeIntent()
    data object Refresh : ManagerHomeIntent()
}

sealed class ManagerHomeEffect {
    data class ShowSnackbar(val message: String) : ManagerHomeEffect()
    data object PostCreated : ManagerHomeEffect()
}

private const val TAG = "ManagerHomeViewModel"

@HiltViewModel
class ManagerHomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val eventRepository: EventRepository
) : MviViewModel<ManagerHomeState, ManagerHomeIntent, ManagerHomeEffect>() {

    override fun createInitialState() = ManagerHomeState()

    init {
        processIntent(ManagerHomeIntent.LoadPosts)
        processIntent(ManagerHomeIntent.LoadEvents)
    }

    override fun handleIntent(intent: ManagerHomeIntent) {
        when (intent) {
            is ManagerHomeIntent.LoadPosts -> loadPosts()
            is ManagerHomeIntent.LoadEvents -> loadEvents()
            is ManagerHomeIntent.CreatePost -> createPost(intent.content, intent.eventId, intent.imageUrl)
            is ManagerHomeIntent.Refresh -> refresh()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            setState { copy(posts = UiState.Loading) }
            postRepository.getPosts().fold(
                onSuccess = { posts ->
                    Log.d(TAG, "Posts loaded: ${posts.size}")
                    setState { copy(posts = UiState.Success(posts)) }
                },
                onFailure = { e -> setState { copy(posts = UiState.Error(e.message ?: "Failed")) } }
            )
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            setState { copy(events = UiState.Loading) }
            eventRepository.getEvents().fold(
                onSuccess = { events ->
                    Log.d(TAG, "Events loaded: ${events.size}")
                    setState { copy(events = UiState.Success(events)) }
                },
                onFailure = { e -> setState { copy(events = UiState.Error(e.message ?: "Failed")) } }
            )
        }
    }

    private fun createPost(content: String, eventId: Int?, imageUrl: String?) {
        viewModelScope.launch {
            val request = CreatePostRequest(content = content, eventId = eventId, imageUrl = imageUrl)
            postRepository.createPost(request).fold(
                onSuccess = {
                    sendEffect(ManagerHomeEffect.PostCreated)
                    loadPosts()
                },
                onFailure = { e ->
                    sendEffect(ManagerHomeEffect.ShowSnackbar(e.message ?: "Failed to create post"))
                }
            )
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }
            loadPosts()
            loadEvents()
            setState { copy(isRefreshing = false) }
        }
    }
}
