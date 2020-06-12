package at.robbert.frontend

import kotlinx.coroutines.delay
import kotlin.browser.window

external val redirectTo: String

suspend fun main() {
    delay(200)
    while (true) {
        doRedirect()
        delay(500)
    }
}

fun doRedirect() {
    window.location.href = redirectTo
}
