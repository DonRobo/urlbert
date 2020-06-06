package at.robbert.redirector.data

import kotlinx.serialization.*
import kotlin.js.Date
import kotlin.math.roundToLong

@Serializable
actual class Timestamp actual constructor(time: Long) {
    val date = Date(time)

    @Serializer(forClass = Timestamp::class)
    companion object : KSerializer<Timestamp> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor(
                "Timestamp",
                PrimitiveKind.LONG
            )

        override fun deserialize(decoder: Decoder): Timestamp {
            return Timestamp(decoder.decodeLong())
        }

        override fun serialize(encoder: Encoder, value: Timestamp) {
            encoder.encodeLong(value.date.getTime().roundToLong())
        }
    }
}
