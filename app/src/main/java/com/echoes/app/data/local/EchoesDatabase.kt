package com.echoes.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.echoes.app.data.local.converter.DatabaseConverters
import com.echoes.app.data.local.dao.CapsuleDao
import com.echoes.app.data.local.dao.CommentDao
import com.echoes.app.data.local.dao.FavoriteDao
import com.echoes.app.data.local.dao.UnlockConditionDao
import com.echoes.app.data.local.dao.UserDao
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.entity.CommentEntity
import com.echoes.app.data.local.entity.FavoriteEntity
import com.echoes.app.data.local.entity.UnlockConditionEntity
import com.echoes.app.data.local.entity.UserEntity

/**
 * Room database definition for the Echoes application.
 *
 * Declares five entities ([UserEntity], [CapsuleEntity], [UnlockConditionEntity],
 * [FavoriteEntity], [CommentEntity]) and exposes DAO accessors for each table.
 * Schema version history is exported to `app/schemas/` for migration verification.
 *
 * The database instance is obtained through [DatabaseProvider], which ensures a
 * single Room instance is shared application-wide.
 */
@Database(
    entities = [
        UserEntity::class,
        CapsuleEntity::class,
        UnlockConditionEntity::class,
        FavoriteEntity::class,
        CommentEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class EchoesDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun capsuleDao(): CapsuleDao

    abstract fun unlockConditionDao(): UnlockConditionDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun commentDao(): CommentDao

    companion object {
        const val DATABASE_NAME = "echoes.db"
    }
}
