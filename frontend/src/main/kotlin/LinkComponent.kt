import at.robbert.redirector.LinkService
import at.robbert.redirector.data.Link
import at.robbert.redirector.data.LinkCondition
import at.robbert.redirector.data.MultiLink
import at.robbert.redirector.data.PLATFORM_OTHER
import kotlinx.coroutines.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import react.*
import react.dom.div

fun RBuilder.linksComponent(handler: RProps.() -> Unit = {}): ReactElement {
    return child(LinkComponent::class) {
        this.attrs(handler)
    }
}

external interface LinksState : RState {
    var multiLinks: List<MultiLink>?
}

class LinkComponent : RComponent<RProps, LinksState>() {
    private val scope = CoroutineScope(GlobalScope.coroutineContext)

    override fun componentDidMount() {
        console.log("Links component loaded")
        scope.launch {
            console.log("Retrieving links")
            val links = LinkService.listLinks()
            setState {
                multiLinks = links
            }
        }
    }

    override fun componentWillUnmount() {
        scope.cancel()
    }

    @ImplicitReflectionSerializer
    override fun RBuilder.render() {
        div {
            +"Here be links"
            for (multiLink in state.multiLinks ?: emptyList()) {
                div {
                    key = multiLink.name
                    +Json.Default.toJson(MultiLink::class.serializer(), multiLink).toString()
                }
            }
        }
    }
}
