package cc.imorning.common.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

object TimeUtils {

    private const val TAG = "TimeUtils"

    const val DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    const val DEFAULT_TIME_FORMAT = "HH:mm:ss"

    /**
     * get format time
     * @param dateTime A datetime like '2022-08-16T16:45:23.320Z'
     * @param format default format is "yyyy-MM-dd HH:mm:ss"
     * @param hoursOffset default offset is +8
     * @return like '2022-08-17 00:45:23'
     */
    fun getFormatDateTime(
        dateTime: DateTime,
        format: String = DEFAULT_DATETIME_FORMAT,
        hoursOffset: Int = +8
    ): String {
        return DateTimeFormat.forPattern(format)
            .withZone(DateTimeZone.forOffsetHours(hoursOffset))
            .print(dateTime)
    }

    /**
     * Returns the formatted date and time as a string.
     * @param timeMillis the time in milliseconds (default value is current time)
     * @param format the desired format of the date and time (default value is DEFAULT_DATETIME_FORMAT)
     * @param hoursOffset the offset in hours from UTC (default value is +8 for Beijing time)
     * @return the formatted date and time as a string
     */
    fun getFormatDateTime(
        timeMillis: Long = System.currentTimeMillis(),
        format: String = DEFAULT_DATETIME_FORMAT,
        hoursOffset: Int = +8
    ): String {
        return DateTimeFormat.forPattern(format)
            .withZone(DateTimeZone.forOffsetHours(hoursOffset))
            .print(millisToDateTime(timeMillis))
    }

    fun millisToDateTime(millis: Long): DateTime {
        return Instant(millis).toDateTime()
    }

    /**
     * return time if today
     */
    fun isToday(time: Long): Boolean {
        val today = LocalDate()
        val date = DateTime(time).toLocalDate()
        return date.equals(today)
    }

}