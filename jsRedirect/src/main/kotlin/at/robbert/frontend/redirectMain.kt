package at.robbert.frontend

import kotlinx.coroutines.delay
import kotlin.browser.document
import kotlin.browser.window

external val redirectTo: String
external val alternative: String

fun log(text: String) {
    val logger = document.getElementById("log")
    if (logger != null) {
        val tDiv = document.createElement("div")
        val textNode = document.createTextNode(text)
        tDiv.appendChild(textNode)
        logger.appendChild(tDiv)
    }
    console.log(text)
}

suspend fun main() {
    log("Waiting 500ms")
    delay(500)
    doRedirect(redirectTo)
    window.setTimeout({
        log("Redirect to app($redirectTo) didn't work after 1000ms. Redirecting to $alternative")
        doRedirect(alternative)
    }, 1000)
}

fun doRedirect(url: String) {
    log("Redirecting to $url")
    window.location.href = url
}
