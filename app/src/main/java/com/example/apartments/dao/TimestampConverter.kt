import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.Date

object TimestampConverter {
    @JvmStatic
    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? {
        return value?.toDate()?.time
    }

    @JvmStatic
    @TypeConverter
    fun dateToTimestamp(date: Long?): Timestamp? {
        return date?.let { Timestamp(Date(it)) }
    }
}