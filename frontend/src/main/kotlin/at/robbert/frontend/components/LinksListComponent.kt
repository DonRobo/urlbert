package at.robbert.frontend.components

import at.robbert.frontend.components.Styles.flexColumn
import at.robbert.redirector.LinkService
import at.robbert.redirector.data.MultiLink
import at.robbert.redirector.data.minusIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import react.*
import react.dom.div

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
        updateLinks()
    }

    private fun updateLinks() {
        scope.launch {
            val links = LinkService.listLinks()
            setState {
                multiLinks = links
            }
        }
    }

    @ImplicitReflectionSerializer
    override fun RBuilder.render() {
        div(flexColumn) {
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
                        this.createLink = {
                            scope.launch {
                                LinkService.updateMultiLink(multiLink.copy(links = multiLink.links.plus(it)))
                                updateLinks()
                            }
                        }
                    }
                }
            }
        }
    }
}
