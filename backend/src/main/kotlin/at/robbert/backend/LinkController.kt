package at.robbert.backend

import at.robbert.redirector.data.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class LinkController(private val linkService: LinkService) {

    @GetMapping("/api/links")
    suspend fun retrieveLinks(): List<MultiLink> {
        return linkService.retrieveLinks()
    }

    @PutMapping("/api/link")
    suspend fun updateLink(@RequestBody multiLink: MultiLink): MultiLink {
        return linkService.addOrUpdateLink(multiLink)
    }

    @GetMapping("/link/{linkName}", produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun accessLink(
        @PathVariable linkName: String,
        @RequestHeader(value = "user-agent") userAgent: String
    ): ResponseEntity<Nothing> {
        val platform = when {
            userAgent.contains("android", ignoreCase = true) -> PLATFORM_ANDROID
            userAgent.containsAny(listOf("iphone", "ipad", "ipod"), ignoreCase = true) -> PLATFORM_IOS
            else -> PLATFORM_OTHER
        }
        val link: Link = linkService.retrieveLink(linkName, platform)
        log.debug("Retrieved link: $link")
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
            .headers {
                it["Location"] = link.url
            }.build()
    }

}

private fun String.containsAny(substrings: List<String>, ignoreCase: Boolean): Boolean = substrings.any { subString ->
    this.contains(subString, ignoreCase = ignoreCase)
}
