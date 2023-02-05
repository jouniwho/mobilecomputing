package com.example.app3.data.repository

import com.example.app3.data.entity.User
import com.example.app3.data.room.UserDao
import kotlinx.coroutines.flow.Flow


class UserRepository(
    private val userDao: UserDao
) {
    fun users(): Flow<List<User>> = userDao.users()
    fun getUsersWithId(userId: Long): User? = userDao.getUserWithId(userId)
     /**
     * Add a user to the user database if it does not exist
     *
     * @return the id of the newly added/created user
     */
    suspend fun addUser(user: User): Long {
    return when (val local = userDao.getUserWithName(user.username)) {
        null -> userDao.insert(user)
        else -> local.id
        }
    }
}