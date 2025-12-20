package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.mapper.toDomainModel
import com.taqsiim.compusconnect.data.mapper.toEntity
import com.taqsiim.compusconnect.data.model.CreatePostRequest
import com.taqsiim.compusconnect.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val api: ApiService,
    private val dao: CampusDao
) {
    
    suspend fun getPosts(): Result<List<Post>> {
        return try {
            val posts = api.getPosts()
            dao.refreshPosts(posts.map { it.toEntity() })
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Note: This should only be called by Club Managers
    suspend fun createPost(request: CreatePostRequest): Result<Post> {
        return try {
            val post = api.createPost(request)
            dao.insertPosts(listOf(post.toEntity()))
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun likePost(postId: Int): Result<Post> {
        return try {
            val post = api.likePost(postId)
            dao.insertPosts(listOf(post.toEntity()))
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unlikePost(postId: Int): Result<Post> {
        return try {
            val post = api.unlikePost(postId)
            dao.insertPosts(listOf(post.toEntity()))
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Local Data Access
    fun getPostsLocal(): Flow<List<Post>> {
        return dao.getPosts().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
