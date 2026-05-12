package com.taqsiim.compusconnect.data.mapper

import com.taqsiim.compusconnect.data.local.entity.*
import com.taqsiim.compusconnect.data.model.*

fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        role = role?.name ?: UserRole.STUDENT.name,
        userName = userName,
        email = email,
        firstName = firstName,
        lastName = lastName,
        faculty = faculty,
        major = major,
        level = level,
        phone = phone ?: "",
        pictureUrl = pictureUrl ?: "",
        inDorms = inDorms,
        hasClub = hasClub
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
        pictureUrl = pictureUrl,
        inDorms = inDorms,
        hasClub = hasClub
    )
}

fun Club.toEntity(): ClubEntity {
    return ClubEntity(
        id = id,
        name = name,
        description = description,
        email = email,
        logo = logo,
        cover = cover,
        followersCount = followersCount,
        members = members,
        eventNumber = eventNumber,
        sessionsNumber = sessionsNumber,
        postsNumber = postsNumber,
        clubAdminName = clubAdminName,
        status = status.name,
        isJoined = isJoined
    )
}

fun ClubEntity.toDomainModel(): Club {
    return Club(
        id = id,
        name = name,
        description = description,
        email = email,
        logo = logo,
        cover = cover,
        followersCount = followersCount,
        members = members,
        eventNumber = eventNumber,
        sessionsNumber = sessionsNumber,
        postsNumber = postsNumber,
        clubAdminName = clubAdminName,
        status = ClubStatus.valueOf(status),
        isJoined = isJoined
    )
}

fun Event.toEntity(): EventEntity {
    return EventEntity(
        eventId = eventId,
        clubName = clubName,
        clubLogoUrl = clubLogoUrl ?: "",
        clubCoverUrl = clubCoverUrl ?: "",
        type = type.name,
        title = title,
        description = description,
        startTime = startTime,
        endTime = endTime,
        location = location ?: "",
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
