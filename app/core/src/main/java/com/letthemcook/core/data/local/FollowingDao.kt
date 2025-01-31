package com.letthemcook.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.letthemcook.core.domain.model.local.Following

@Dao
interface FollowingDao {

    @Query("SELECT * FROM Following WHERE userId = :userId LIMIT 1")
    suspend fun getFollowingByUserId(userId: String): Following?

    @Upsert
    suspend fun upsertFollowing(following: Following)

    @Delete
    suspend fun deleteFollowing(following: Following)
}