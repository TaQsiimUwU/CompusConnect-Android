package com.taqsiim.compusconnect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.local.entity.*
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        ClubEntity::class,
        EventEntity::class,
        PostEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun campusDao(): CampusDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_profile_new (
                        userId INTEGER NOT NULL PRIMARY KEY,
                        role TEXT NOT NULL,
                        userName TEXT NOT NULL,
                        email TEXT NOT NULL,
                        firstName TEXT NOT NULL,
                        lastName TEXT NOT NULL,
                        faculty TEXT NOT NULL,
                        major TEXT NOT NULL,
                        level TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        pictureUrl TEXT NOT NULL,
                        inDorms INTEGER NOT NULL DEFAULT 0,
                        hasClub INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO user_profile_new (
                        userId, role, userName, email, firstName, lastName,
                        faculty, major, level, phone, pictureUrl, inDorms, hasClub
                    )
                    SELECT
                        userId, role, userName, email, firstName, lastName,
                        faculty, major, CAST(level AS TEXT), phone, pictureUrl, 0, 0
                    FROM user_profile
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE user_profile")
                db.execSQL("ALTER TABLE user_profile_new RENAME TO user_profile")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE user_profile_new (
                        userId INTEGER NOT NULL PRIMARY KEY,
                        role TEXT NOT NULL,
                        userName TEXT NOT NULL,
                        email TEXT NOT NULL,
                        firstName TEXT NOT NULL,
                        lastName TEXT NOT NULL,
                        faculty TEXT NOT NULL,
                        major TEXT NOT NULL,
                        level TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        pictureUrl TEXT NOT NULL,
                        inDorms INTEGER NOT NULL,
                        hasClub INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO user_profile_new (userId, role, userName, email, firstName, lastName, faculty, major, level, phone, pictureUrl, inDorms, hasClub)
                    SELECT userId, role, userName, email, firstName, lastName, faculty, major, level, phone, pictureUrl,
                           CAST(inDorms AS INTEGER),
                           hasClub
                    FROM user_profile
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE user_profile")
                db.execSQL("ALTER TABLE user_profile_new RENAME TO user_profile")
            }
        }
    }
}
