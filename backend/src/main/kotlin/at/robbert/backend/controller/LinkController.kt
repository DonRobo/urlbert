package at.robbert.backend.controller

import at.robbert.backend.service.GeoLocationService
import at.robbert.backend.service.LinkService
import at.robbert.backend.util.log
import at.robbert.backend.util.notFound
import at.robbert.redirector.data.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import java.net.InetAddress

@RestController
class LinkController(private val linkService: LinkService, private val geoLocationService: GeoLocationService) {

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
            "Invalid multilink: $multiLink"
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @GetMapping("/link/{linkName}", produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun accessLink(
        @PathVariable linkName: String,
        @RequestHeader(value = "user-agent") userAgent: String,
        @RequestHeader(value = "X-Forwarded-For") remoteIp: String,
        request: ServerHttpRequest
    ): ResponseEntity<String> {
        val country = geoLocationService.locate(InetAddress.getByName(remoteIp))

        log.debug("Redirecting request link/$linkName")
        request.headers.forEach { (key, value) ->
            log.debug("\t$key: ${value.joinToString(", ")}")
        }
        log.debug("\tCountry: $country")
        val platform = when {
            userAgent.contains("android", ignoreCase = true) -> PLATFORM_ANDROID
            userAgent.containsAny(listOf("iphone", "ipad", "ipod"), ignoreCase = true) -> PLATFORM_IOS
            else -> PLATFORM_OTHER
        }
        log.debug("\trequest from: ${request.remoteAddress}")
        val link: Link = linkService.retrieveLink(linkName, platform, country)
        log.debug("\tredirecting to: ${link.url} using ${link.redirection}")
        return coroutineScope {
            launch {
                linkService.linkClicked(linkName, link.url)
            }
            return@coroutineScope when (link.redirection.method) {
                RedirectMethod.HTTP -> ResponseEntity.status(link.redirection.status ?: error("Link malformed"))
                    .headers {
                        it["Location"] = link.url
                        it["Vary"] = "User-Agent"
                        it["X-Frame-Options"] = "SAMEORIGIN"
                        it["X-Content-Type-Options"] = "nosniff"
                        it["Content-Type"] = "text/html; charset=utf-8"
                    }.build()
                RedirectMethod.JS -> executeJavascriptRedirect(
                    link,
                    linkService.retrieveLink(linkName, PLATFORM_OTHER, country)
                )
                RedirectMethod.FAST_JS -> executeFastJavascriptRedirect(
                    link,
                    linkService.retrieveLink(linkName, PLATFORM_OTHER, country)
                )
            }
        }

    }

    private fun executeJavascriptRedirect(link: Link, default: Link): ResponseEntity<String> {
        return ResponseEntity.status(200).headers {
            it["Vary"] = "User-Agent"
            it["X-Frame-Options"] = "SAMEORIGIN"
            it["X-Content-Type-Options"] = "nosniff"
            it["Content-Type"] = "text/html; charset=utf-8"
        }.body(
            "<!DOCTYPE html>\n" + createHTML().apply {
                html {
                    head {
                        title {
                            +"MBR Link"
                        }
                    }
                    body {
//                        div {
//                            +"Loading..."
//                        }
                        div {
                            id = "log"
                        }
                        script {
                            unsafe {
                                +"const redirectTo='${link.url.escapeJsString()}';"
                                +"const alternative='${default.url.escapeJsString()}';"
                            }
                        }
                        script {
                            src = "/jsRedirect.js"
                        }
                    }
                }
            }.finalize()
        )
    }

    private fun executeFastJavascriptRedirect(link: Link, default: Link): ResponseEntity<String> {
        return ResponseEntity.status(200).headers {
            it["Vary"] = "User-Agent"
            it["X-Frame-Options"] = "SAMEORIGIN"
            it["X-Content-Type-Options"] = "nosniff"
            it["Content-Type"] = "text/html; charset=utf-8"
        }.body(
            "<!DOCTYPE html>\n" + createHTML().apply {
                html {
                    head {
                        title {
                            +"MBR Link"
                        }
                    }
                    body {
                        script {
                            unsafe {
                                //language=JavaScript
                                +"""
const redirectTo='${link.url.escapeJsString()}';
const alternative='${default.url.escapeJsString()}';
setTimeout(()=>{
    window.location.href = redirectTo;
    setTimeout(()=>{
        window.location.href = alternative;
    }, 1500);
}, 350)
                                """.trimIndent()
                            }
                        }
                    }
                }
            }.finalize()
        )
    }

    @GetMapping("/link/*/{linkName}", produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun accessLinkWithPath(
        @PathVariable linkName: String,
        @RequestHeader(value = "user-agent") userAgent: String,
        @RequestHeader(value = "X-Forwarded-For") remoteIp: String,
        request: ServerHttpRequest
    ): ResponseEntity<String> {
        return accessLink(linkName, userAgent, remoteIp, request)
    }

}

private fun String.containsAny(substrings: List<String>, ignoreCase: Boolean): Boolean = substrings.any { subString ->
    this.contains(subString, ignoreCase = ignoreCase)
}
