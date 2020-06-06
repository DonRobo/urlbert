package at.robbert.backend

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.boot.jackson.JsonComponent
import java.sql.Timestamp

@JsonComponent
class TimestampSerializer : JsonSerializer<Timestamp>() {
    override fun serialize(value: Timestamp?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (value != null)
            gen.writeNumber(value.time)
        else
            gen.writeNull()
    }

}
