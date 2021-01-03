package at.robbert.redirector.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

expect class UUID

@Serializable
data class UpdatePasswordPayload(val password: String, @Contextual val secret: UUID)
