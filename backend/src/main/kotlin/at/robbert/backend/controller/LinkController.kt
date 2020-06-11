package at.robbert.backend.controller

import at.robbert.backend.service.LinkService
import at.robbert.backend.util.log
import at.robbert.backend.util.notFound
import at.robbert.redirector.data.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
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
        return ml ?: notFound()
    }

    @PostMapping("/api/link")
    suspend fun addLink(@RequestBody multiLink: MultiLink): MultiLink {
        require(multiLink.name.isNotBlank()) { "Invalid name" }
        validateMultiLink(multiLink)
        return linkService.addLink(multiLink)
    }

    private fun validateMultiLink(multiLink: MultiLink) {
        require(multiLink.links.all { link -> link.url.isNotBlank() && link.conditions.all { condition -> condition.isValid } }) {
            "Invalid multilink"
        }
    }

    @GetMapping("/link/{linkName}", produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun accessLink(
        @PathVariable linkName: String,
        @RequestHeader(value = "user-agent") userAgent: String,
        request: ServerHttpRequest
    ): ResponseEntity<Nothing> {
        log.debug("Redirecting request link/$linkName")
        request.headers.forEach { (key, value) ->
            log.debug("\t$key: ${value.joinToString(", ")}")
        }
        val platform = when {
            userAgent.contains("android", ignoreCase = true) -> PLATFORM_ANDROID
            userAgent.containsAny(listOf("iphone", "ipad", "ipod"), ignoreCase = true) -> PLATFORM_IOS
            else -> PLATFORM_OTHER
        }
        log.debug("\trequest from: ${request.remoteAddress}")
        val link: Link = linkService.retrieveLink(linkName, platform)
        log.debug("\tredirecting to: ${link.url}")
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
            .headers {
                it["Location"] = link.url
                it["Vary"] = "User-Agent"
                it["X-Frame-Options"] = "SAMEORIGIN"
                it["X-Content-Type-Options"] = "nosniff"
                it["Content"] = "text/html; charset=utf-8"
            }.build()
    }

}

private fun String.containsAny(substrings: List<String>, ignoreCase: Boolean): Boolean = substrings.any { subString ->
    this.contains(subString, ignoreCase = ignoreCase)
}
