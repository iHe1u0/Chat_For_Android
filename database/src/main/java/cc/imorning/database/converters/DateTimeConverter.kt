package cc.imorning.database.converters

import androidx.room.TypeConverter
import org.joda.time.DateTime


class DateTimeConverter {

    @TypeConverter
    fun revertObject(dateTimeString: String?): DateTime {
        return DateTime(dateTimeString)
    }

    @TypeConverter
    fun converterObject(dateTime: DateTime?): String {
        return dateTime.toString()
    }
}