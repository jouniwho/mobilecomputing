package com.example.app3.data.room

import androidx.room.*
import com.example.app3.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReminderDao {
    @Query("""
        SELECT reminders.* FROM reminders
        INNER JOIN categories ON reminders.reminder_category_id= categories.id
        WHERE reminder_category_id = :categoryId
    """)
    abstract fun remindersFromCategory(categoryId: Long): Flow<List<ReminderToCategory>>

    @Query("""SELECT * FROM reminders WHERE id = :reminderId""")
    abstract fun reminder(reminderId: Long): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Reminder): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Reminder)

    @Delete
    abstract suspend fun delete(entity: Reminder)
}
