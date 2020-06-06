package at.robbert.redirector.data

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable

expect annotation class Id()
expect class Timestamp(time: Long)

const val CONDITION_TYPE_PLATFORM = "PLATFORM"
const val CONDITION_TYPE_COUNTRY = "COUNTRY"

const val PLATFORM_ANDROID = "ANDROID"
const val PLATFORM_IOS = "IOS"
const val PLATFORM_OTHER = "OTHER"

@Serializable
data class LinkCondition(val conditionType: String, val conditionValue: String)

@Serializable
data class Link(val conditions: List<LinkCondition>, val url: String)

@Serializable
data class MultiLink(@Id val name: String, val links: List<Link>, @ContextualSerialization val createdAt: Timestamp)
