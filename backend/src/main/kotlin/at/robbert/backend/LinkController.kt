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
        validateMultiLink(multiLink)
        return linkService.updateLink(multiLink)
    }

    @DeleteMapping("/api/link/{linkName}")
    suspend fun deleteLink(@PathVariable linkName: String): MultiLink {
        val ml = linkService.retrieveMultiLink(linkName)
        linkService.deleteLink(linkName)
        return ml
    }

    @PostMapping("/api/link")
    suspend fun addLink(@RequestBody multiLink: MultiLink): MultiLink {
        require(multiLink.name.isNotBlank()) { "Invalid name" }
        validateMultiLink(multiLink)
        return linkService.addLink(multiLink)
    }

    private fun validateMultiLink(multiLink: MultiLink) {
        require(multiLink.links.all { link -> link.url.isNotBlank() && link.conditions.all { condition -> condition.isValid } }) { "Invalid multilink" }
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
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
            .headers {
                it["Location"] = link.url
            }.build()
    }

}

private fun String.containsAny(substrings: List<String>, ignoreCase: Boolean): Boolean = substrings.any { subString ->
    this.contains(subString, ignoreCase = ignoreCase)
}
