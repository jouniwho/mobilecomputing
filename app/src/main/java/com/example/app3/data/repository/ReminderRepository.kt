package com.example.app3.data.repository


import com.example.app3.data.entity.Reminder
import com.example.app3.data.room.ReminderDao
import com.example.app3.data.room.ReminderToCategory
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Reminder] instances
 */
class ReminderRepository(
    private val reminderDao: ReminderDao
) {
    /**
     * Returns a flow containing the list of payments associated with the category with the
     * given [categoryId]
     */
    fun remindersInCategory(categoryId: Long) : Flow<List<ReminderToCategory>> {
        return reminderDao.remindersFromCategory(categoryId)
    }

    /**
     * Add a new [Reminder]
     */
    suspend fun addReminder(message: Reminder) = reminderDao.insert(message)
    //Edit reminder
    suspend fun editReminder(message: Reminder) = reminderDao.update(message)
    //delete
    suspend fun deleteReminder(message: Reminder) = reminderDao.delete(message)
}