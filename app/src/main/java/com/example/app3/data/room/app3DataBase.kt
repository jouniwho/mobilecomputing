package com.example.app3.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.app3.data.entity.Category
import com.example.app3.data.entity.Payment
import com.example.app3.data.entity.User

/**
 * The [RoomDatabase] for this app
 */
@Database(
    entities = [Category::class, Payment::class, User::class],
    version = 3,
    exportSchema = false
)

abstract class app3DataBase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun paymentDao(): PaymentDao
    abstract fun userDao(): UserDao
}