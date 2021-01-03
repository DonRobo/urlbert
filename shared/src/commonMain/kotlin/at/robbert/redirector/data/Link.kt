@file:UseSerializers(TimestampSerializer::class)

package at.robbert.redirector.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

expect annotation class Id()

expect class Timestamp(time: Long)

const val CONDITION_TYPE_PLATFORM = "PLATFORM"
const val CONDITION_TYPE_COUNTRY = "COUNTRY"

const val PLATFORM_ANDROID = "ANDROID"
const val PLATFORM_IOS = "IOS"
const val PLATFORM_OTHER = "OTHER"

@Serializable
data class LinkCondition(val conditionType: String, val conditionValue: String)

val LinkCondition.isValid
    get() = when (conditionType) {
        CONDITION_TYPE_PLATFORM -> conditionValue in setOf(PLATFORM_ANDROID, PLATFORM_IOS, PLATFORM_OTHER)
        CONDITION_TYPE_COUNTRY -> conditionValue.isNotBlank()
        else -> false
    }

enum class RedirectMethod {
    HTTP, JS, FAST_JS
}

@Serializable
data class Redirection(val method: RedirectMethod, val status: Int?)

@Serializable
data class Link(
    val conditions: List<LinkCondition>,
    val url: String,
    val redirection: Redirection = Redirection(RedirectMethod.HTTP, 301)
)

@Serializable
data class ClickCounts(
    val allTime: Int,
    val last24h: Int
)

@Serializable
data class MultiLink(
    @Id val name: String,
    val links: List<Link>,
    val createdAt: Timestamp? = null,
    val stats: ClickCounts
)
