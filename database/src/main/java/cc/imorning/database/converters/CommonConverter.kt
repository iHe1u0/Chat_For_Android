package cc.imorning.database.converters

import androidx.room.TypeConverter
import org.joda.time.DateTime

class CommonConverter {

    @TypeConverter
    fun revertDateTime(dateTimeString: String?): DateTime {
        return DateTime(dateTimeString)
    }

    @TypeConverter
    fun converterDateTime(dateTime: DateTime?): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun revertBoolean(value: Int): Boolean {
        return value == 1
    }

    @TypeConverter
    fun converterBoolean(boolean: Boolean): Int {
        return if (boolean) {
            1
        } else {
            0
        }
    }

    @TypeConverter
    fun converterLong(value: Long): String {
        return value.toString()
    }

    @TypeConverter
    fun revertLong(value: String): Long {
        return value.toLong()
    }
}