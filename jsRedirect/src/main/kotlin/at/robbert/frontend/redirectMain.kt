package at.robbert.frontend

import kotlin.browser.document
import kotlin.browser.window

external val redirectTo: String
external val alternative: String

fun delay(delay: Int, block: () -> Unit) {
    window.setTimeout({
        block()
    }, delay)
}

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

fun main() {
    log("Waiting 500ms")
    delay(500) {
        doRedirect(redirectTo)
        delay(2000) {
            log("Redirect to app($redirectTo) didn't work after 1000ms. Redirecting to $alternative")
            doRedirect(alternative)
        }
    }
}

fun doRedirect(url: String) {
    log("Redirecting to $url")
    delay(1000) {
        window.location.href = url
    }
}
