package com.taqsiim.compusconnect.ui.clubManager.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.CreatePostRequest
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.repository.ClubRepository
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
    data class DeletePost(val postId: Int) : ManagerHomeIntent()
    data class UpdatePost(val postId: Int, val content: String) : ManagerHomeIntent()
    data class LikePost(val postId: Int) : ManagerHomeIntent()
    data class UnlikePost(val postId: Int) : ManagerHomeIntent()
    data class AddComment(val postId: Int, val content: String) : ManagerHomeIntent()
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
    private val eventRepository: EventRepository,
    private val clubRepository: ClubRepository
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
            is ManagerHomeIntent.DeletePost -> deletePost(intent.postId)
            is ManagerHomeIntent.UpdatePost -> updatePost(intent.postId, intent.content)
            is ManagerHomeIntent.LikePost -> likePost(intent.postId)
            is ManagerHomeIntent.UnlikePost -> unlikePost(intent.postId)
            is ManagerHomeIntent.AddComment -> addComment(intent.postId, intent.content)
            is ManagerHomeIntent.Refresh -> refresh()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            setState { copy(posts = UiState.Loading) }
            postRepository.getPosts().fold(
                onSuccess = { posts ->
                    Log.d(TAG, "Posts loaded: ${posts.size}")
                    val enriched = enrichPostsWithClubData(posts)
                    setState { copy(posts = UiState.Success(enriched)) }
                },
                onFailure = { e -> setState { copy(posts = UiState.Error(e.message ?: "Failed")) } }
            )
        }
    }

    private suspend fun enrichPostsWithClubData(posts: List<Post>): List<Post> {
        return try {
            val clubs = clubRepository.getClubs().getOrNull() ?: return posts
            val clubMap = clubs.associateBy { it.id }
            posts.map { post ->
                val club = clubMap[post.clubId]
                post.copy(
                    clubName = club?.name,
                    clubLogoUrl = club?.logo
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enrich posts: ${e.message}")
            posts
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
                    sendEffect(ManagerHomeEffect.ShowSnackbar("Post published!"))
                    loadPosts()
                },
                onFailure = { e ->
                    sendEffect(ManagerHomeEffect.ShowSnackbar(e.message ?: "Failed to create post"))
                }
            )
        }
    }

    private fun deletePost(postId: Int) {
        viewModelScope.launch {
            // Optimistic removal
            val currentPosts = (currentState.posts as? UiState.Success)?.data ?: return@launch
            setState { copy(posts = UiState.Success(currentPosts.filter { it.postId != postId })) }
            postRepository.deletePost(postId).fold(
                onSuccess = {
                    sendEffect(ManagerHomeEffect.ShowSnackbar("Post deleted"))
                },
                onFailure = { e ->
                    // Revert
                    setState { copy(posts = UiState.Success(currentPosts)) }
                    sendEffect(ManagerHomeEffect.ShowSnackbar(e.message ?: "Failed to delete"))
                }
            )
        }
    }

    private fun updatePost(postId: Int, content: String) {
        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) {
            viewModelScope.launch {
                sendEffect(ManagerHomeEffect.ShowSnackbar("Post content cannot be empty"))
            }
            return
        }

        val currentPosts = (currentState.posts as? UiState.Success)?.data ?: return
        val targetPost = currentPosts.find { it.postId == postId } ?: return
        val updatedPosts = currentPosts.map { post ->
            if (post.postId == postId) post.copy(content = trimmedContent) else post
        }
        setState { copy(posts = UiState.Success(updatedPosts)) }

        viewModelScope.launch {
            postRepository.updatePost(postId, trimmedContent).fold(
                onSuccess = {
                    sendEffect(ManagerHomeEffect.ShowSnackbar("Post updated"))
                },
                onFailure = { e ->
                    setState {
                        copy(
                            posts = UiState.Success(
                                currentPosts.map { post ->
                                    if (post.postId == postId) targetPost else post
                                }
                            )
                        )
                    }
                    sendEffect(ManagerHomeEffect.ShowSnackbar(e.message ?: "Failed to update post"))
                }
            )
        }
    }

    private fun likePost(postId: Int) {
        val currentPosts = (currentState.posts as? UiState.Success)?.data ?: return
        val updatedPosts = currentPosts.map { post ->
            if (post.postId == postId) post.copy(isLiked = true, likeCount = post.likeCount + 1) else post
        }
        setState { copy(posts = UiState.Success(updatedPosts)) }

        viewModelScope.launch {
            postRepository.likePost(postId).fold(
                onSuccess = { },
                onFailure = {
                    setState { copy(posts = UiState.Success(currentPosts)) }
                    sendEffect(ManagerHomeEffect.ShowSnackbar("Failed to like post"))
                }
            )
        }
    }

    private fun unlikePost(postId: Int) {
        val currentPosts = (currentState.posts as? UiState.Success)?.data ?: return
        val updatedPosts = currentPosts.map { post ->
            if (post.postId == postId) post.copy(
                isLiked = false,
                likeCount = (post.likeCount - 1).coerceAtLeast(0)
            ) else post
        }
        setState { copy(posts = UiState.Success(updatedPosts)) }

        viewModelScope.launch {
            postRepository.unlikePost(postId).fold(
                onSuccess = { },
                onFailure = {
                    setState { copy(posts = UiState.Success(currentPosts)) }
                    sendEffect(ManagerHomeEffect.ShowSnackbar("Failed to unlike post"))
                }
            )
        }
    }

    private fun addComment(postId: Int, content: String) {
        viewModelScope.launch {
            postRepository.addComment(postId, content).fold(
                onSuccess = {
                    val currentPosts = (currentState.posts as? UiState.Success)?.data
                    if (currentPosts != null) {
                        val updatedPosts = currentPosts.map { post ->
                            if (post.postId == postId) {
                                post.copy(commentCount = post.commentCount + 1)
                            } else {
                                post
                            }
                        }
                        setState { copy(posts = UiState.Success(updatedPosts)) }
                    }
                    sendEffect(ManagerHomeEffect.ShowSnackbar("Comment added"))
                },
                onFailure = { e ->
                    sendEffect(ManagerHomeEffect.ShowSnackbar(e.message ?: "Failed to add comment"))
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
