package com.example.app3.data.room

import androidx.room.*
import com.example.app3.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao {

    @Query(value = "SELECT * FROM users WHERE username = :username")
    abstract suspend fun getUserWithName(username: String): User

    @Query("SELECT * FROM users WHERE id = :userId")
    abstract fun getUserWithId(userId: Long): User?

    @Query("SELECT * FROM users LIMIT 15")
    abstract fun users(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<User>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: User)

    @Delete
    abstract suspend fun delete(entity: User)
}