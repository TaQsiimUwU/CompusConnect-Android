package com.taqsiim.compusconnect.utils

object Constants {
    // API Configuration
    const val BASE_URL = "https://campus-connect-backend-three.vercel.app/"
    object Endpoints {
        const val LOGIN = "api/auth/login"

        // Users
        const val USER_PROFILE = "api/users/me"
        // Clubs
        const val CLUBS = "api/clubs"
        const val CLUB_BY_ID = "api/clubs/{id}"
        const val CLUB_FOLLOW = "api/clubs/{id}/follow"
        const val CLUB_REPORT = "api/clubs/report"

        // Events & Sessions (using events endpoint)
        const val EVENTS = "api/events"
        const val EVENTS_REQUESTED = "api/events/requested" // this just for the club manager
        const val EVENT_BY_ID = "api/events/{event_id}"
        const val EVENT_REGISTER = "api/events/{event_id}/register"
        const val EVENT_REGISTERED_STUDENTS = "api/events/{event_id}/registered_students" // this for club manager
        const val EVENT_ATTENDANCE = "api/events/{event_id}/attendance_list" // this for club manager
        const val EVENT_POSTS = "api/events/{id}/posts"
        const val EVENT_REPORT = "api/events/report"

        // Posts
        const val POSTS = "api/posts"
        const val POST_BY_ID = "api/posts/{id}"
        const val POST_LIKE = "api/posts/{id}/like"
        const val POST_COMMENTS = "api/posts/{id}/comments"

        // Rooms
        const val ROOMS = "api/rooms"
        const val ROOMS_RESERVE = "api/rooms/reserve"
        const val ROOMS_CANCEL = "api/rooms/{id}/cancel"
        const val ROOM_REPORT = "api/rooms/report"
        const val ROOMS_RESOURCES = "api/rooms/resources"

        // Facilities
        const val FACILITIES = "api/facilities"
        const val FACILITIES_RESERVE = "api/facilities/{id}/reserve"
        const val FACILITY_REPORT = "api/facilities/report"

        // User specific
        const val USER_RESERVATIONS = "api/reservations"
    }
}