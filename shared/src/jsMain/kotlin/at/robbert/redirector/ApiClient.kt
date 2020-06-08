package at.robbert.redirector

import at.robbert.redirector.data.MultiLink
import kotlinx.coroutines.await
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
//import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import kotlin.browser.window
import kotlin.js.json

external fun encodeURIComponent(str: String): String

fun String.encodeUriComponent(): String = encodeURIComponent(this)

object LinkService {
    suspend fun listLinks(): List<MultiLink> {
        return ApiClient.get(MultiLink.serializer().list, "links")
    }

    suspend fun updateMultiLink(multiLink: MultiLink): MultiLink {
        return ApiClient.put(MultiLink.serializer(), "link", multiLink, MultiLink.serializer())
    }

    suspend fun addMultiLink(name: String): MultiLink {
        return ApiClient.post(MultiLink.serializer(), "link", MultiLink(name, emptyList()), MultiLink.serializer())
    }

    suspend fun deleteMultiLink(name: String): MultiLink {
        return ApiClient.delete(MultiLink.serializer(), "link/${name.encodeUriComponent()}")
    }
}

@OptIn(UnstableDefault::class)
object ApiClient {
    suspend fun <T> get(deserializationStrategy: DeserializationStrategy<T>, apiUrl: String): T {
        val fullUrl = "/api/$apiUrl"
        val json = window.fetch(
            fullUrl, RequestInit(
                "GET", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                ), credentials = "same-origin".asDynamic()
            )
        ).await().text().await()
        return Json.parse(deserializationStrategy, json)
    }

    suspend fun <T> delete(deserializationStrategy: DeserializationStrategy<T>, apiUrl: String): T {
        val fullUrl = "/api/$apiUrl"
        val json = window.fetch(
            fullUrl, RequestInit(
                "DELETE", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                ), credentials = "same-origin".asDynamic()
            )
        ).await().text().await()
        return Json.parse(deserializationStrategy, json)
    }

    suspend fun <T, V> put(
        deserializationStrategy: DeserializationStrategy<T>,
        apiUrl: String,
        body: V,
        serializationStrategy: SerializationStrategy<V>
    ): T {
        val fullUrl = "/api/$apiUrl"
        val json = window.fetch(
            fullUrl, RequestInit(
                "PUT", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                ), credentials = "same-origin".asDynamic(),
                body = Json.stringify(serializationStrategy, body)
            )
        ).await().text().await()
        return Json.parse(deserializationStrategy, json)
    }

    suspend fun <T, V> post(
        deserializationStrategy: DeserializationStrategy<T>,
        apiUrl: String,
        body: V,
        serializationStrategy: SerializationStrategy<V>
    ): T {
        val fullUrl = "/api/$apiUrl"
        val json = window.fetch(
            fullUrl, RequestInit(
                "POST", headers = json(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"
                ), credentials = "same-origin".asDynamic(),
                body = Json.stringify(serializationStrategy, body)
            )
        ).await().text().await()
        return Json.parse(deserializationStrategy, json)
    }

}
