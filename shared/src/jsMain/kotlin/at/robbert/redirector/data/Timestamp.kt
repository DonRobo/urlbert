package at.robbert.redirector.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.Date
import kotlin.math.roundToLong

@Serializable
actual class Timestamp actual constructor(time: Long) {
    val date = Date(time)

    @Serializer(forClass = Timestamp::class)
    companion object : KSerializer<Timestamp> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor(
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
