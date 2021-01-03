package at.robbert.redirector.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

actual object TimestampSerializer : KSerializer<Timestamp> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            "Timestamp",
            PrimitiveKind.LONG
        )

    override fun deserialize(decoder: Decoder): Timestamp {
        return Timestamp(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeLong(value.time)
    }
}
