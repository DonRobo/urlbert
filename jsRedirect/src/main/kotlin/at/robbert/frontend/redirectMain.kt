package at.robbert.frontend

import kotlinx.coroutines.delay
import kotlin.browser.window

external val redirectTo: String
external val alternative: String

suspend fun main() {
    delay(500)
    doRedirect(redirectTo)
    window.setTimeout({
        console.log("Redirect to app($redirectTo) didn't work. Redirecting to $alternative")
        doRedirect(alternative)
    }, 1000)
}

fun doRedirect(url: String) {
    window.location.href = url
}
