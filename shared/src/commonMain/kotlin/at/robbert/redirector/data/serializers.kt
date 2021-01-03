package at.robbert.redirector.data

import kotlinx.serialization.KSerializer

expect object TimestampSerializer : KSerializer<Timestamp>
