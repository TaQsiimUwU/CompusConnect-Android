package com.taqsiim.compusconnect.ui.student.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.model.Reservation
import com.taqsiim.compusconnect.data.repository.ClubRepository
import com.taqsiim.compusconnect.data.repository.PostRepository
import com.taqsiim.compusconnect.data.repository.UserRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/* ──────────────────────── State ──────────────────────── */

data class HomeState(
    val posts: UiState<List<Post>> = UiState.Loading,
    val reservations: UiState<List<Reservation>> = UiState.Idle,
    val isRefreshing: Boolean = false
)

/* ──────────────────────── Intent ─────────────────────── */

sealed class HomeIntent {
    data object LoadPosts : HomeIntent()
    data object LoadReservations : HomeIntent()
    data class LikePost(val postId: Int) : HomeIntent()
    data class UnlikePost(val postId: Int) : HomeIntent()
    data object Refresh : HomeIntent()
}

/* ──────────────────────── Effect ─────────────────────── */

sealed class HomeEffect {
    data class ShowSnackbar(val message: String) : HomeEffect()
}

/* ──────────────────────── ViewModel ──────────────────── */

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val clubRepository: ClubRepository,
    private val userRepository: UserRepository
) : MviViewModel<HomeState, HomeIntent, HomeEffect>() {

    override fun createInitialState() = HomeState()

    init {
        processIntent(HomeIntent.LoadPosts)
        processIntent(HomeIntent.LoadReservations)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadPosts -> loadPosts()
            is HomeIntent.LoadReservations -> loadReservations()
            is HomeIntent.LikePost -> likePost(intent.postId)
            is HomeIntent.UnlikePost -> unlikePost(intent.postId)
            is HomeIntent.Refresh -> refresh()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            Log.d(TAG, "Loading posts...")
            setState { copy(posts = UiState.Loading) }

            postRepository.getPosts().fold(
                onSuccess = { posts ->
                    Log.d(TAG, "Posts loaded: ${posts.size}")
                    // Enrich posts with club name and logo
                    val enrichedPosts = enrichPostsWithClubData(posts)
                    setState { copy(posts = UiState.Success(enrichedPosts)) }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load posts: ${error.message}")
                    setState { copy(posts = UiState.Error(error.message ?: "Failed to load posts")) }
                }
            )
        }
    }

    private suspend fun enrichPostsWithClubData(posts: List<Post>): List<Post> {
        val clubsResult = clubRepository.getClubs()
        val clubMap = clubsResult.getOrNull()
            ?.associateBy { it.id }
            ?: return posts // If clubs fail to load, return posts as-is

        return posts.map { post ->
            val club = clubMap[post.clubId]
            post.copy(
                clubName = club?.name ?: "Club ${post.clubId}",
                clubLogoUrl = club?.logo ?: ""
            )
        }
    }

    private fun loadReservations() {
        viewModelScope.launch {
            Log.d(TAG, "Loading reservations...")
            setState { copy(reservations = UiState.Loading) }

            userRepository.getMyReservations().fold(
                onSuccess = { reservations ->
                    Log.d(TAG, "Reservations loaded: ${reservations.size}")
                    setState { copy(reservations = UiState.Success(reservations)) }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load reservations: ${error.message}")
                    setState { copy(reservations = UiState.Error(error.message ?: "Failed to load reservations")) }
                }
            )
        }
    }

    /**
     * Optimistic like: immediately toggle UI state, then confirm with API.
     * On failure, revert the toggle.
     */
    private fun likePost(postId: Int) {
        // Optimistic update
        val currentPosts = (currentState.posts as? UiState.Success)?.data ?: return
        val updatedPosts = currentPosts.map { post ->
            if (post.postId == postId) post.copy(isLiked = true, likeCount = post.likeCount + 1)
            else post
        }
        setState { copy(posts = UiState.Success(updatedPosts)) }

        viewModelScope.launch {
            postRepository.likePost(postId).fold(
                onSuccess = { /* already updated optimistically */ },
                onFailure = {
                    // Revert on failure
                    setState { copy(posts = UiState.Success(currentPosts)) }
                    sendEffect(HomeEffect.ShowSnackbar("Failed to like post"))
                }
            )
        }
    }

    /**
     * Optimistic unlike: immediately toggle UI state, then confirm with API.
     * On failure, revert the toggle.
     */
    private fun unlikePost(postId: Int) {
        // Optimistic update
        val currentPosts = (currentState.posts as? UiState.Success)?.data ?: return
        val updatedPosts = currentPosts.map { post ->
            if (post.postId == postId) post.copy(isLiked = false, likeCount = (post.likeCount - 1).coerceAtLeast(0))
            else post
        }
        setState { copy(posts = UiState.Success(updatedPosts)) }

        viewModelScope.launch {
            postRepository.unlikePost(postId).fold(
                onSuccess = { /* already updated optimistically */ },
                onFailure = {
                    // Revert on failure
                    setState { copy(posts = UiState.Success(currentPosts)) }
                    sendEffect(HomeEffect.ShowSnackbar("Failed to unlike post"))
                }
            )
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }
            loadPosts()
            loadReservations()
            setState { copy(isRefreshing = false) }
        }
    }
}
