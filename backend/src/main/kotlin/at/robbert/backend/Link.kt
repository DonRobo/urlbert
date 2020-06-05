package at.robbert.backend

import org.springframework.data.annotation.Id

const val PLATFORM_ANDROID = "ANDROID"
const val PLATFORM_IOS = "IOS"
const val PLATFORM_OTHER = "OTHER"

data class LinkCondition(val conditionType: String, val conditionValue: String)
data class Link(val conditions: List<LinkCondition>, val url: String)
data class MultiLink(@Id val name: String, val links: List<Link>)
