package at.robbert.redirector.data

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

expect class UUID

@Serializable
data class UpdatePasswordPayload(val password: String, @ContextualSerialization val secret: UUID)
