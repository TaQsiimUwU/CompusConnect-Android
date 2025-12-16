package com.taqsiim.compusconnect.data.mapper

import com.taqsiim.compusconnect.data.local.entity.*
import com.taqsiim.compusconnect.data.model.*

// --- User Mappers ---
fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        role = role.name,
        userName = userName,
        email = email,
        firstName = firstName,
        lastName = lastName,
        faculty = faculty,
        major = major,
        level = level,
        phone = phone,
        pictureUrl = pictureUrl
    )
}

fun UserEntity.toDomainModel(): User {
    return User(
        userId = userId,
        role = UserRole.valueOf(role),
        userName = userName,
        email = email,
        firstName = firstName,
        lastName = lastName,
        faculty = faculty,
        major = major,
        level = level,
        phone = phone,
        pictureUrl = pictureUrl
    )
}

// --- Club Mappers ---
fun Club.toEntity(): ClubEntity {
    return ClubEntity(
        clubId = clubId,
        name = name,
        description = description,
        status = status.name,
        isJoined = isJoined,
        logo = logo,
        cover = cover,
        noOfEvents = noOfEvents,
        clubManagerName = clubManagerName,
        noOfFollowers = noOfFollowers
    )
}

fun ClubEntity.toDomainModel(): Club {
    return Club(
        clubId = clubId,
        name = name,
        description = description,
        status = ClubStatus.valueOf(status),
        isJoined = isJoined,
        logo = logo,
        cover = cover,
        noOfEvents = noOfEvents,
        clubManagerName = clubManagerName,
        noOfFollowers = noOfFollowers
    )
}

// --- Event Mappers ---
fun Event.toEntity(): EventEntity {
    return EventEntity(
        eventId = eventId,
        clubName = clubName,
        clubLogoUrl = clubLogoUrl,
        clubCoverUrl = clubCoverUrl,
        type = type.name,
        title = title,
        description = description,
        startTime = startTime,
        endTime = endTime,
        location = location,
        registrations = noOfRegistrations,
        maxRegistrations = noOfMaxRegistrations
    )
}

fun EventEntity.toDomainModel(): Event {
    return Event(
        eventId = eventId,
        clubName = clubName,
        clubLogoUrl = clubLogoUrl,
        clubCoverUrl = clubCoverUrl,
        type = EventType.valueOf(type),
        title = title,
        description = description,
        startTime = startTime,
        endTime = endTime,
        location = location,
        noOfRegistrations = registrations,
        noOfMaxRegistrations = maxRegistrations
    )
}

// --- Post Mappers ---
fun Post.toEntity(): PostEntity {
    return PostEntity(
        postId = postId,
        clubId = clubId,
        eventId = eventId,
        content = content,
        imageUrl = imageUrl,
        createdAt = createdAt,
        likeCount = likeCount,
        commentCount = commentCount,
        isLiked = isLiked
    )
}

fun PostEntity.toDomainModel(): Post {
    return Post(
        postId = postId,
        clubId = clubId,
        eventId = eventId,
        content = content,
        imageUrl = imageUrl,
        createdAt = createdAt,
        likeCount = likeCount,
        commentCount = commentCount,
        isLiked = isLiked
    )
}
