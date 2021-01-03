package at.robbert.backend.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.Condition
import org.jooq.Field
import org.jooq.JSONB
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime

fun currentTimestamp(): Timestamp {
    return Timestamp.valueOf(LocalDateTime.now())
}

fun LocalDateTime.toTimestamp(): Timestamp {
    return Timestamp.valueOf(this)
}

inline fun <reified T> ObjectMapper.toObject(jsonb: JSONB): T {
    return this.readValue(jsonb.data())
}

fun <T> ObjectMapper.toJsonB(value: T): JSONB {
    return JSONB.valueOf(this.writeValueAsString(value))
}

fun Field<Timestamp>.youngerThan(duration: Duration): Condition {
    return this.greaterOrEqual((LocalDateTime.now() - duration).toTimestamp())
}
