package at.robbert.frontend.components

import at.robbert.frontend.lib.Styles.*
import at.robbert.frontend.lib.button
import at.robbert.frontend.lib.div
import at.robbert.redirector.data.Link
import at.robbert.redirector.data.LinkCondition
import at.robbert.redirector.data.MultiLink
import at.robbert.redirector.data.containsDuplicates
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.css.FontWeight
import kotlinx.css.fontWeight
import react.*
import react.dom.a
import styled.css
import styled.styledDiv

fun RBuilder.multiLink(handler: MultiLinkProps.() -> Unit): ReactElement {
    return child(MultiLinkComponent::class) {
        this.attrs(handler)
    }
}

external interface MultiLinkProps : RProps {
    var multiLink: MultiLink
    var deleteLink: (Int) -> Job
    var createLink: (Link) -> Job
    var deleteMultiLink: () -> Job
}

external interface MultiLinkState : RState {
    var creatingNew: Boolean
    var newLink: String
    var newConditions: List<LinkCondition>
}

class MultiLinkComponent : RComponent<MultiLinkProps, MultiLinkState>() {
    init {
        state.creatingNew = false
    }

    override fun RBuilder.render() {
        div(flexColumn, shadowedBox, flexNoStretch) {
            div(flexRow, flexCenter) {
                button("x") {
                    props.deleteMultiLink()
                }
                styledDiv {
                    css {
                        fontWeight = FontWeight.bold
                    }
                    val link = "${window.location.origin}/link/${props.multiLink.name}"
                    a(link) {
                        +link
                    }
                }
            }
            div {
                +"All time clicks: ${props.multiLink.stats.allTime}"
            }
            div {
                +"Last 24h clicks: ${props.multiLink.stats.last24h}"
            }
            props.multiLink.links.mapIndexed { index, link -> link to index }
                .sortedByDescending { it.first.conditions.size }.forEach { (link, index) ->
                    displayLink {
                        this.link = link
                        this.deleteLink = { props.deleteLink(index) }
                    }
                }
            if (props.multiLink.links.map { it.conditions }.containsDuplicates()) {
                div(warning) {
                    +"Warning! There are links where it's not clear which one should be chosen!"
                }
            }
            if (!state.creatingNew)
                div(flexRow) {
                    button("Add new") {
                        setState {
                            creatingNew = true
                            newLink = ""
                            newConditions = emptyList()
                        }
                    }
                }
            else
                createLink {
                    this.cancel = {
                        setState { creatingNew = false }
                    }
                    this.createLink = {
                        GlobalScope.launch {
                            props.createLink(it).join()
                            setState {
                                creatingNew = false
                            }
                        }
                    }
                }
        }
    }
}

