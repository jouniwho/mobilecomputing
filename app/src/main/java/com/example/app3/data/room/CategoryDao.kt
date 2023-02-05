package com.example.app3.data.room

import androidx.room.*
import com.example.app3.data.entity.Category
import kotlinx.coroutines.flow.Flow


@Dao
abstract class CategoryDao {

    @Query(value = "SELECT * FROM categories WHERE name = :name")
    abstract suspend fun getCategoryWithName(name: String): Category

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    abstract fun getCategoryWithId(categoryId: Long): Category?

    @Query("SELECT * FROM categories LIMIT 15")
    abstract fun categories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<Category>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Category)

    @Delete
    abstract suspend fun delete(entity: Category): Int
}