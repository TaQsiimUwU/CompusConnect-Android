package com.taqsiim.compusconnect.ui.student.posts

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Comment
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.repository.PostRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostDetailState(
    val post: UiState<Post> = UiState.Loading,
    val comments: UiState<List<Comment>> = UiState.Idle,
    val isSubmitting: Boolean = false
)

sealed class PostDetailIntent {
    data class LoadPost(val postId: Int) : PostDetailIntent()
    data class LoadComments(val postId: Int) : PostDetailIntent()
    data class AddComment(val postId: Int, val content: String) : PostDetailIntent()
    data class LikePost(val postId: Int) : PostDetailIntent()
    data class UnlikePost(val postId: Int) : PostDetailIntent()
}

sealed class PostDetailEffect {
    data class ShowSnackbar(val message: String) : PostDetailEffect()
    data object CommentAdded : PostDetailEffect()
}

private const val TAG = "PostDetailViewModel"

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository
) : MviViewModel<PostDetailState, PostDetailIntent, PostDetailEffect>() {

    override fun createInitialState() = PostDetailState()

    override fun handleIntent(intent: PostDetailIntent) {
        when (intent) {
            is PostDetailIntent.LoadPost -> loadPost(intent.postId)
            is PostDetailIntent.LoadComments -> loadComments(intent.postId)
            is PostDetailIntent.AddComment -> addComment(intent.postId, intent.content)
            is PostDetailIntent.LikePost -> likePost(intent.postId)
            is PostDetailIntent.UnlikePost -> unlikePost(intent.postId)
        }
    }

    private fun loadPost(postId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Loading post: $postId")
            setState { copy(post = UiState.Loading) }
            postRepository.getPostById(postId).fold(
                onSuccess = { post ->
                    Log.d(TAG, "Post loaded: ${post.postId}")
                    setState { copy(post = UiState.Success(post)) }
                },
                onFailure = { e ->
                    Log.e(TAG, "Failed to load post: ${e.message}")
                    setState { copy(post = UiState.Error(e.message ?: "Failed to load post")) }
                }
            )
        }
    }

    private fun loadComments(postId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Loading comments for post: $postId")
            setState { copy(comments = UiState.Loading) }
            postRepository.getComments(postId).fold(
                onSuccess = { comments -> setState { copy(comments = UiState.Success(comments)) } },
                onFailure = { e -> setState { copy(comments = UiState.Error(e.message ?: "Failed")) } }
            )
        }
    }

    private fun addComment(postId: Int, content: String) {
        viewModelScope.launch {
            setState { copy(isSubmitting = true) }
            postRepository.addComment(postId, content).fold(
                onSuccess = {
                    setState { copy(isSubmitting = false) }
                    // Reload comments and update comment count on post
                    loadComments(postId)
                    val currentPost = (currentState.post as? UiState.Success)?.data
                    if (currentPost != null) {
                        setState { copy(post = UiState.Success(currentPost.copy(commentCount = currentPost.commentCount + 1))) }
                    }
                    sendEffect(PostDetailEffect.CommentAdded)
                },
                onFailure = { e ->
                    setState { copy(isSubmitting = false) }
                    sendEffect(PostDetailEffect.ShowSnackbar(e.message ?: "Failed to add comment"))
                }
            )
        }
    }

    private fun likePost(postId: Int) {
        // Optimistic update
        val currentPost = (currentState.post as? UiState.Success)?.data ?: return
        setState { copy(post = UiState.Success(currentPost.copy(isLiked = true, likeCount = currentPost.likeCount + 1))) }

        viewModelScope.launch {
            postRepository.likePost(postId).fold(
                onSuccess = { },
                onFailure = {
                    // Revert
                    setState { copy(post = UiState.Success(currentPost)) }
                    sendEffect(PostDetailEffect.ShowSnackbar("Failed to like post"))
                }
            )
        }
    }

    private fun unlikePost(postId: Int) {
        // Optimistic update
        val currentPost = (currentState.post as? UiState.Success)?.data ?: return
        setState { copy(post = UiState.Success(currentPost.copy(isLiked = false, likeCount = (currentPost.likeCount - 1).coerceAtLeast(0)))) }

        viewModelScope.launch {
            postRepository.unlikePost(postId).fold(
                onSuccess = { },
                onFailure = {
                    // Revert
                    setState { copy(post = UiState.Success(currentPost)) }
                    sendEffect(PostDetailEffect.ShowSnackbar("Failed to unlike post"))
                }
            )
        }
    }
}
