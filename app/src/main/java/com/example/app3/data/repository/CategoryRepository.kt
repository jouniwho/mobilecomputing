package com.example.app3.data.repository

import com.example.app3.data.entity.Category
import com.example.app3.data.room.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun categories(): Flow<List<Category>> = categoryDao.categories()

    /**
     * Add a category to the database if it does not exist
     *
     * @return the id of the newly added/created category
     */
    suspend fun addCategory(category: Category): Long {
        return when (val local = categoryDao.getCategoryWithName(category.name)) {
            null -> categoryDao.insert(category)
            else -> local.id
        }
    }
}