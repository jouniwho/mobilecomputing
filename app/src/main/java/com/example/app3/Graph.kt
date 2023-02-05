package com.example.app3

import android.content.Context
import androidx.room.Room
import com.example.app3.data.repository.CategoryRepository
import com.example.app3.data.repository.PaymentRepository
import com.example.app3.data.repository.UserRepository
import com.example.app3.data.room.app3DataBase

/**
 * A simple singleton dependency graph
 *
 * For a real app, please use something like Koin/Dagger/Hilt instead
 */
object Graph {
    lateinit var database: app3DataBase

    val categoryRepository by lazy {
        CategoryRepository(
            categoryDao = database.categoryDao()
        )
    }

    val paymentRepository by lazy {
        PaymentRepository(
            paymentDao = database.paymentDao()
        )
    }

    val userRepository by lazy {
        UserRepository(
            userDao = database.userDao()
        )
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, app3DataBase::class.java, "mcData.db")
            .fallbackToDestructiveMigration() // don't use this in production app
            .build()
    }
}