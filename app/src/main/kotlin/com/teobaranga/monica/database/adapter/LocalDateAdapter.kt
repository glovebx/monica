package com.teobaranga.monica.database.adapter

import androidx.room.TypeConverter
import java.time.LocalDate

object LocalDateAdapter {

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(value)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }
}
