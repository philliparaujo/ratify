package com.example.ratify.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CountDao {
    @Insert
    suspend fun insert(count: Count)

    @Update
    suspend fun update(count: Count)

    @Delete
    suspend fun delete(count: Count)

    @Query("SELECT * FROM count")
    fun get(): Flow<List<Count>>

    @Query("SELECT COUNT(*) FROM count")
    fun numEntries(): Flow<Int>
}