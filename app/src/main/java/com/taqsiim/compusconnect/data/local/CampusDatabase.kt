package com.taqsiim.compusconnect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        ClubEntity::class,
        EventEntity::class,
        PostEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun campusDao(): CampusDao
}
