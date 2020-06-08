package at.robbert.frontend.components

import at.robbert.frontend.components.Styles.*
import at.robbert.redirector.LinkService
import at.robbert.redirector.data.MultiLink
import at.robbert.redirector.data.minusIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import react.*


fun RBuilder.linkList(handler: RProps.() -> Unit = {}): ReactElement {
    return child(LinksListComponent::class) {
        this.attrs(handler)
    }
}

external interface LinksListState : RState {
    var multiLinks: List<MultiLink>?
    var addingNew: String
}

class LinksListComponent : RComponent<RProps, LinksListState>() {
    private val scope = CoroutineScope(GlobalScope.coroutineContext)

    init {
        state.addingNew = ""
    }

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
                        this.deleteMultiLink = {
                            scope.launch {
                                console.log("what")
                                LinkService.deleteMultiLink(multiLink.name)
                                updateLinks()
                            }
                        }
                    }
                }
            }
            div(flexColumn, shadowedBox, flexNoStretch) {
                fun submitFunction() {
                    scope.launch {
                        LinkService.addMultiLink(state.addingNew)
                        updateLinks()
                        setState {
                            addingNew = ""
                        }
                    }
                }
                formInput("Short-URL ending", state.addingNew, { submitFunction() }, InputType.text) {
                    setState {
                        addingNew = it
                    }
                }
                button("Add new multilink") {
                    submitFunction()
                }
            }
        }
    }
}
