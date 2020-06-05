package at.robbert.redirector

import at.robbert.redirector.data.MultiLink
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import kotlin.browser.window
import kotlin.js.json

object LinkService {
    suspend fun listLinks(): List<MultiLink> {
        return ApiClient.get("link/")
    }
}

object ApiClient {
    suspend fun <T> get(apiUrl: String): T {
        val fullUrl = "/api/$apiUrl"
        val json = window.fetch(
            fullUrl, RequestInit(
                "GET", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                ), credentials = "same-origin".asDynamic()
            )
        ).await().text().await()
        return JSON.parse(json)
    }

}
