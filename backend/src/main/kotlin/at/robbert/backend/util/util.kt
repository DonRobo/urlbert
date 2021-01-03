package at.robbert.backend.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.JSONB
import java.sql.Timestamp
import java.time.LocalDateTime

fun currentTimestamp(): Timestamp {
    return Timestamp.valueOf(LocalDateTime.now())
}

inline fun <reified T> ObjectMapper.toObject(jsonb: JSONB): T {
    return this.readValue(jsonb.data())
}

fun <T> ObjectMapper.toJsonB(value: T): JSONB {
    return JSONB.valueOf(this.writeValueAsString(value))
}
