package com.echoes.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.echoes.app.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE capsuleId = :capsuleId AND userId = :userId")
    suspend fun deleteFavorite(capsuleId: String, userId: String)

    @Query("DELETE FROM favorites WHERE capsuleId = :capsuleId")
    suspend fun deleteFavoritesForCapsule(capsuleId: String)

    @Query("SELECT COUNT(*) FROM favorites WHERE capsuleId = :capsuleId AND userId = :userId")
    suspend fun favoriteCount(capsuleId: String, userId: String): Int
}
