package com.letthemcook.core.domain.dataConvertion.typeConvertion

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeConverter {

    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    @TypeConverter
    fun dateTimeToString(dateTime: LocalDateTime): String = dateTime.format(formatter)

    @TypeConverter
    fun stringToDateTime(string: String): LocalDateTime = LocalDateTime.parse(string, formatter)
}