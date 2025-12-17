package com.taqsiim.compusconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.taqsiim.compusconnect.data.model.*

/**
 * ViewModel for Club Manager Mode
 * TODO: Add state declarations
 * TODO: Implement all functions
 */
class ManagerViewModel() : ViewModel() {

    // TODO: Add StateFlow declarations for posts, requests, attendees
    // TODO: Add loading and error states

    // TODO: Implement initialization

    // Posts
    fun loadPosts() {
        TODO("Implement load posts")
    }

    fun createPost(post: Post) {
        TODO("Implement create post")
    }

    // Events
    fun createEvent(event: Event) {
        TODO("Implement create event")
    }

    // Sessions
//    fun createSession(session: Session) {
//        TODO("Implement create session")
//    }

    // Requests
    fun loadRequests() {
        TODO("Implement load requests")
    }

    // Attendees
    fun loadEventAttendees(eventId: String) {
        TODO("Implement load attendees")
    }

    fun clearError() {
        TODO("Implement clear error")
    }
}
