import at.robbert.redirector.LinkService
import at.robbert.redirector.data.MultiLink
import at.robbert.redirector.data.minusIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.serialization.ImplicitReflectionSerializer
import react.*
import react.dom.div
import styled.css
import styled.styledDiv

fun RBuilder.linkList(handler: RProps.() -> Unit = {}): ReactElement {
    return child(LinksListComponent::class) {
        this.attrs(handler)
    }
}


external interface LinksListState : RState {
    var multiLinks: List<MultiLink>?
}

class LinksListComponent : RComponent<RProps, LinksListState>() {
    private val scope = CoroutineScope(GlobalScope.coroutineContext)

    override fun componentDidMount() {
        console.log("Links component loaded")
        updateLinks()
    }

    private fun updateLinks() {
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
        styledDiv {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
            }
            for (multiLink in state.multiLinks ?: emptyList()) {
                div {
                    key = multiLink.name
                    multiLink {
                        this.multiLink = multiLink
                        this.deleteLink = {
                            scope.launch {
                                LinkService.updateMultiLink(multiLink.copy(links = multiLink.links.minusIndex(it)))
                                updateLinks()
                            }
                        }
                    }
                }
            }
        }
    }
}
