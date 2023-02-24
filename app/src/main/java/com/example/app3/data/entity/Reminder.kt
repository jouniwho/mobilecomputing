package com.example.app3.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    indices = [
        Index("id", unique = true),
        Index("reminder_category_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["reminder_category_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Reminder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "message") val message: String = "",
    @ColumnInfo(name = "location_x") val locationX: Float = 0.0f,
    @ColumnInfo(name = "location_y") val locationY: Float = 0.0f,
    @ColumnInfo(name = "reminder_time") val reminderTime: String = "",
    @ColumnInfo(name = "reminder_date") val reminderDate: String = "",
    @ColumnInfo(name = "creation_time") val reminderCreation: String = "",
    @ColumnInfo(name = "creator_id") val creatorId: Long = 0,
    @ColumnInfo(name = "reminder_seen") val reminderSeen: Boolean = false,
    @ColumnInfo(name = "reminder_category_id") val reminderCategoryId: Long,
    @ColumnInfo(name = "reminder_category") val reminderCategory: String = "",
    @ColumnInfo(name = "notification") val notification: Boolean = false
)
