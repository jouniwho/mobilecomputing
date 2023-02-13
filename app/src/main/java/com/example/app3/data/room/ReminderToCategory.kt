package com.example.app3.data.room

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.example.app3.data.entity.Category
import com.example.app3.data.entity.Reminder
import java.util.*

class ReminderToCategory {
    @Embedded
    lateinit var remainder: Reminder

    @Relation(parentColumn = "reminder_category_id", entityColumn = "id")
    lateinit var _categories: List<Category>

    @get:Ignore
    val category: Category
        get() = _categories[0]

    /**
     * Allow this class to be destructured by consumers
     */
    operator fun component1() = remainder
    operator fun component2() = category

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ReminderToCategory -> remainder == other.remainder && _categories == other._categories
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(remainder, _categories)
}
