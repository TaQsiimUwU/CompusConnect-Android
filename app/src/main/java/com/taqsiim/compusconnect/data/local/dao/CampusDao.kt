package com.taqsiim.compusconnect.data.local.dao

import androidx.room.*
import com.taqsiim.compusconnect.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {

    // --- User ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Query("DELETE FROM user_profile")
    suspend fun clearUser()

    @Transaction
    suspend fun refreshUser(user: UserEntity) {
        clearUser()
        insertUser(user)
    }

    // --- Clubs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClubs(clubs: List<ClubEntity>)

    @Query("SELECT * FROM clubs")
    fun getClubs(): Flow<List<ClubEntity>>

    @Query("DELETE FROM clubs")
    suspend fun clearClubs()

    @Transaction
    suspend fun refreshClubs(clubs: List<ClubEntity>) {
        clearClubs()
        insertClubs(clubs)
    }

    // --- Events ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getEvents(): Flow<List<EventEntity>>

    @Query("DELETE FROM events")
    suspend fun clearEvents()

    @Query("DELETE FROM events WHERE type = :type")
    suspend fun clearEventsByType(type: String)

    @Transaction
    suspend fun refreshEventsByType(events: List<EventEntity>, type: String) {
        clearEventsByType(type)
        insertEvents(events)
    }

    // --- Posts ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getPosts(): Flow<List<PostEntity>>

    @Query("DELETE FROM posts")
    suspend fun clearPosts()

    @Transaction
    suspend fun refreshPosts(posts: List<PostEntity>) {
        clearPosts()
        insertPosts(posts)
    }
}
