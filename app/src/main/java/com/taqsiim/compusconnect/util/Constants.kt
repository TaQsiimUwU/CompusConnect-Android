package com.taqsiim.compusconnect.util

object Constants {
    // API Configuration
    const val BASE_URL = "https://your-api-url.com/api/" // Replace with actual API URL
    const val TIMEOUT = 30L // seconds
    
    // SharedPreferences Keys
    const val PREFS_NAME = "campus_connect_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_AUTH_TOKEN = "auth_token"
    
    // Navigation Routes
    object Routes {
        // Student Routes
        const val STUDENT_HOME = "student/home"
        const val STUDENT_EVENTS = "student/events"
        const val STUDENT_CLUBS = "student/clubs"
        const val STUDENT_PROFILE = "student/profile"
        const val STUDENT_FACILITIES = "student/facilities"
        const val STUDENT_RESERVATIONS = "student/reservations"
        const val STUDENT_NOTIFICATIONS = "student/notifications"
        const val EVENT_DETAIL = "student/event/{eventId}"
        const val SESSION_DETAIL = "student/session/{sessionId}"
        const val CLUB_DETAIL = "student/club/{clubId}"
        
        // Manager Routes
        const val MANAGER_HOME = "manager/home"
        const val MANAGER_REQUESTS = "manager/requests"
        const val MANAGER_ATTENDEES = "manager/attendees"
        const val MANAGER_ACCOUNT = "manager/account"
        const val CREATE_POST = "manager/create-post"
        const val SCHEDULE_EVENT = "manager/schedule-event"
        const val SCHEDULE_SESSION = "manager/schedule-session"
        
        // Auth Routes
        const val LOGIN = "login"
        const val SPLASH = "splash"
    }
    
    // Date/Time Formats
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val TIME_FORMAT = "HH:mm"
    const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm"
    const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
    
    // Time Slots for Facility Booking
    val TIME_SLOTS = listOf(
        "08:00 - 09:00",
        "09:00 - 10:00",
        "10:00 - 11:00",
        "11:00 - 12:00",
        "12:00 - 13:00",
        "13:00 - 14:00",
        "14:00 - 15:00",
        "15:00 - 16:00",
        "16:00 - 17:00",
        "17:00 - 18:00",
        "18:00 - 19:00",
        "19:00 - 20:00"
    )
}
