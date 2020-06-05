import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import react.*
import react.dom.div
import react.dom.render
import kotlin.browser.document
import kotlin.coroutines.CoroutineContext

class AppState : RState {
    var text: String = "Not initialized"
}

class ApplicationComponent : RComponent<RProps, AppState>() {
    init {
        state = AppState()
//        GlobalScope.launch {
//            val response = fetchHello()
//            setState {
//                text = response.text
//            }
//        }
    }

    override fun RBuilder.render() {
        div {
            +"Currently displaying: ${state.text}"
        }
    }

}

//suspend fun fetchHello(): Test {
//    val url = "/api/hello"
//    return withContext(GlobalScope.coroutineContext) {
//        val response = window.fetch(
//            url, RequestInit(
//                "GET", headers = json(
//                    "Accept" to "application/json",
//                    "Content-Type" to "application/json"
//                ), credentials = "same-origin".asDynamic()
//            )
//        ).await()
//        return@withContext parse(Test.serializer(), response.text().await())
//    }
//}

private class Application : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()

    fun start() {
        document.getElementById("root")?.let {
            render(buildElement {
                val el = child(ApplicationComponent::class) {

                }
            }, it)
        }
    }
}

fun main() {
    Application().start()
}
