package at.robbert.backend.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.Field
import org.jooq.JSONB
import org.jooq.impl.DSL
import java.sql.Timestamp
import java.time.LocalDateTime

fun LocalDateTime.toTimestamp(): Timestamp {
    return Timestamp.valueOf(this)
}

inline fun <reified T> ObjectMapper.toObject(jsonb: JSONB): T {
    return this.readValue(jsonb.data())
}

fun <T> ObjectMapper.toJsonB(value: T): JSONB {
    return JSONB.valueOf(this.writeValueAsString(value))
}

fun interval(interval: String): Field<Any> {
    return DSL.field("interval {0}", DSL.inline(interval))
}
