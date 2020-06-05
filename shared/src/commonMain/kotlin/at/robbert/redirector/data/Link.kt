package at.robbert.redirector.data

import kotlinx.serialization.Serializable

expect annotation class Id()

const val PLATFORM_ANDROID = "ANDROID"
const val PLATFORM_IOS = "IOS"
const val PLATFORM_OTHER = "OTHER"

@Serializable
data class LinkCondition(val conditionType: String, val conditionValue: String)

@Serializable
data class Link(val conditions: List<LinkCondition>, val url: String)

@Serializable
data class MultiLink(@Id val name: String, val links: List<Link>)
