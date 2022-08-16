package cc.imorning.common.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

object TimeUtils {

    const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"

    /**
     * get format time
     *
     * @param dateTime A datetime like '2022-08-16T16:45:23.320Z'
     *
     * @param format default format is "yyyy-MM-dd HH:mm:ss"
     *
     * @param hoursOffset default offset is +8
     *
     * @return like '2022-08-17 00:45:23'
     */
    fun getFormatDateTime(
        dateTime: DateTime,
        format: String = DEFAULT_FORMAT,
        hoursOffset: Int = +8
    ): String {
        return DateTimeFormat.forPattern(format)
            .withZone(DateTimeZone.forOffsetHours(hoursOffset))
            .print(dateTime)
    }
}