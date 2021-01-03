package at.robbert.frontend

import kotlinx.browser.document
import kotlinx.browser.window

external val redirectTo: String
external val alternative: String

fun delay(delay: Int, block: () -> Unit) {
    window.setTimeout({
        block()
    }, delay)
}

const val enableHtmlLogging = false

@Suppress("ConstantConditionIf")
fun log(text: String) {
    if (enableHtmlLogging) {
        val logger = document.getElementById("log")
        if (logger != null) {
            val tDiv = document.createElement("div")
            val textNode = document.createTextNode(text)
            tDiv.appendChild(textNode)
            logger.appendChild(tDiv)
        }
    }
    console.log(text)
}

fun main() {
    log("Waiting 250ms")
    delay(250) {
        doRedirect(redirectTo)
        delay(1500) {
            log("Redirect to app($redirectTo) didn't work after 1500ms. Redirecting to $alternative")
            doRedirect(alternative)
        }
    }
}

fun doRedirect(url: String) {
    log("Redirecting to $url")
    delay(100) {
        window.location.href = url
    }
}
