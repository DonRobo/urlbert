package at.robbert.backend.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.boot.jackson.JsonComponent
import java.sql.Timestamp

@JsonComponent
class TimestampSerializer : JsonSerializer<Timestamp>() {
    override fun serialize(value: Timestamp, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeNumber(value.time)
    }

}
