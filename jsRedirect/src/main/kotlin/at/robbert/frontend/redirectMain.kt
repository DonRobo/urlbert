package at.robbert.frontend

import kotlinx.coroutines.delay
import kotlin.browser.window

external val redirectTo: String

suspend fun main() {
    delay(200)
    window.location.href = redirectTo
}
