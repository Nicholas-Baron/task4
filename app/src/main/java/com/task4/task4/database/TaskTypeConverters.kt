@file:Suppress("RedundantNullableReturnType")

package com.task4.task4.database

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class TaskTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? = millisSinceEpoch?.let { Date(it) }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid.toString()

    @TypeConverter
    fun toUUID(uuid: String?): UUID? = UUID.fromString(uuid)
}