package at.robbert.backend.util

import java.sql.Timestamp
import java.time.LocalDateTime

fun currentTimestamp(): Timestamp {
    return Timestamp.valueOf(LocalDateTime.now())
}
