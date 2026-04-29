package com.echoes.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var instance: EchoesDatabase? = null

    fun getDatabase(context: Context): EchoesDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                EchoesDatabase::class.java,
                EchoesDatabase.DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { database ->
                instance = database
            }
        }
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS favorites (
                    capsuleId TEXT NOT NULL,
                    userId TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    PRIMARY KEY(capsuleId, userId)
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS comments (
                    commentId TEXT NOT NULL PRIMARY KEY,
                    capsuleId TEXT NOT NULL,
                    userId TEXT NOT NULL,
                    authorDisplayName TEXT NOT NULL,
                    body TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_comments_capsuleId ON comments(capsuleId)")
        }
    }
}
