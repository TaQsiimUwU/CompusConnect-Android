package com.taqsiim.compusconnect.data.repository

import android.util.Log
import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.mapper.formatDates
import com.taqsiim.compusconnect.data.model.CreatePostRequest
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.model.UpdatePostRequest
import com.taqsiim.compusconnect.data.model.Comment
import com.taqsiim.compusconnect.data.model.CommentRequest
import com.taqsiim.compusconnect.data.model.MessageResponse
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val api: ApiService,
) {

    suspend fun getPosts(): Result<List<Post>> {
        return try {
            val response = api.getPosts()
            val posts = response.newsFeed.map { it.formatDates() }
            if (posts.isNotEmpty()) {
                Log.d(TAG , "Post: ${posts[0]}")
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Note: This should only be called by Club Managers
    suspend fun createPost(request: CreatePostRequest): Result<MessageResponse> {
        return try {
            val response = api.createPost(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun likePost(postId: Int): Result<MessageResponse> {
        return try {
            val response = api.likePost(postId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unlikePost(postId: Int): Result<MessageResponse> {
        return try {
            val response = api.unlikePost(postId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPostsForEvent(eventId: Int): Result<List<Post>> {
        return try {
            val posts = api.getPostsForEvent(eventId).map { it.formatDates() }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePost(postId: Int, newContent: String): Result<MessageResponse> {
        return try {
            val response = api.updatePost(postId, UpdatePostRequest(newContent))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(postId: Int, content: String): Result<MessageResponse> {
        return try {
            val response = api.addComment(postId, CommentRequest(content))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getComments(postId: Int): Result<List<Comment>> {
        return try {
            val response = api.getComments(postId)
            Result.success(response.comments.map { it.formatDates() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    companion object {
        private const val TAG =  "PostRepository"
    }
}
