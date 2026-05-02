package com.echoes.app.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var instance: EchoesDatabase? = null

    fun getDatabase(context: Context): EchoesDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                EchoesDatabase::class.java,
                EchoesDatabase.DATABASE_NAME
            ).build().also { database ->
                instance = database
            }
        }
    }
}
