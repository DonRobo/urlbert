package at.robbert.redirector

import at.robbert.redirector.data.MultiLink
import at.robbert.redirector.data.UUID
import at.robbert.redirector.data.UpdatePasswordPayload
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import kotlin.js.json

external fun encodeURIComponent(str: String): String

fun String.encodeUriComponent(): String = encodeURIComponent(this)

object UserService {
    suspend fun updatePassword(password: String, secret: UUID): Boolean {
        return ApiClient.put(
            Boolean.serializer(),
            "user/setPassword",
            UpdatePasswordPayload(password, secret),
            UpdatePasswordPayload.serializer()
        )
    }
}

object LinkService {
    suspend fun listLinks(): List<MultiLink> {
        return ApiClient.get(ListSerializer(MultiLink.serializer()), "links")
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
        return Json.decodeFromString(deserializationStrategy, json)
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
        return Json.decodeFromString(deserializationStrategy, json)
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
                body = Json.encodeToString(serializationStrategy, body)
            )
        ).await().text().await()
        return Json.decodeFromString(deserializationStrategy, json)
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
                body = Json.encodeToString(serializationStrategy, body)
            )
        ).await().text().await()
        return Json.decodeFromString(deserializationStrategy, json)
    }

}
