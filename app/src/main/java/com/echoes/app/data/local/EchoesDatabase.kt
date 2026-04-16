package com.echoes.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.echoes.app.data.local.converter.DatabaseConverters
import com.echoes.app.data.local.dao.CapsuleDao
import com.echoes.app.data.local.dao.UnlockConditionDao
import com.echoes.app.data.local.dao.UserDao
import com.echoes.app.data.local.entity.CapsuleEntity
import com.echoes.app.data.local.entity.UnlockConditionEntity
import com.echoes.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CapsuleEntity::class,
        UnlockConditionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class EchoesDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun capsuleDao(): CapsuleDao

    abstract fun unlockConditionDao(): UnlockConditionDao

    companion object {
        const val DATABASE_NAME = "echoes.db"
    }
}
