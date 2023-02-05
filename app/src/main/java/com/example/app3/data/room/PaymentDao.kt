package com.example.app3.data.room

import androidx.room.*
import com.example.app3.data.entity.Payment
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PaymentDao {
    @Query("""
        SELECT payments.* FROM payments
        INNER JOIN categories ON payments.payment_category_id = categories.id
        WHERE payment_category_id = :categoryId
    """)
    abstract fun paymentsFromCategory(categoryId: Long): Flow<List<PaymentToCategory>>

    @Query("""SELECT * FROM payments WHERE id = :paymentId""")
    abstract fun payment(paymentId: Long): Payment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Payment): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Payment)

    @Delete
    abstract suspend fun delete(entity: Payment): Int
}
