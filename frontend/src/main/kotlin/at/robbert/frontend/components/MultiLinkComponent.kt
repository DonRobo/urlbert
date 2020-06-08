package at.robbert.frontend.components

import at.robbert.frontend.components.Styles.*
import at.robbert.redirector.data.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.css.FontWeight
import kotlinx.css.fontWeight
import kotlinx.html.InputType
import react.*
import react.dom.a
import react.dom.div
import styled.css
import styled.styledDiv
import kotlin.browser.window

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

    fun RBuilder.renderLink(link: Link, linkIndex: Int): ReactElement =
        div(flexRow, flexStretch, mpMedium) {
            button(buttonLeft, "x") {
                props.deleteLink(linkIndex)
            }
            div(pl1) {
                a(link.url) {
                    +link.url
                }
                if (link.conditions.isNotEmpty()) {
                    link.conditions.forEachIndexed { i, con ->
                        if (i != 0)
                            +"and"
                        condition {
                            condition = con
                        }
                    }
                } else {
                    div {
                        +"Default link"
                    }
                }
            }
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
            props.multiLink.links.mapIndexed { index, link -> link to index }
                .sortedByDescending { it.first.conditions.size }.forEach { (link, index) ->
                    renderLink(link, index)
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
                div(flexColumn, ml3) {
                    val submitFunction = {
                        GlobalScope.launch {
                            props.createLink(Link(state.newConditions.filter { it.isValid }, state.newLink)).join()
                            setState {
                                creatingNew = false
                            }
                        }
                    }
                    formInput("Link", state.newLink, { submitFunction() }, InputType.text) {
                        setState {
                            newLink = it
                        }
                    }
                    state.newConditions.forEachIndexed { index, con ->
                        div(flexRow, mpMedium) {
                            button(buttonLeft, "x") {
                                setState {
                                    this.newConditions = this.newConditions.minusIndex(index)
                                }
                            }
                            div(flexColumn, pl1) {
                                formSelect(
                                    "Condition type",
                                    con.conditionType,
                                    listOf(CONDITION_TYPE_PLATFORM to "Platform")
                                ) {
                                    console.log(con, it)
                                    setState {
                                        this.newConditions = newConditions.set(index, con.copy(conditionType = it))
                                    }
                                }
                                formSelect(
                                    "Condition value", con.conditionValue, listOf(
                                        PLATFORM_ANDROID to "Android",
                                        PLATFORM_IOS to "iOS",
                                        PLATFORM_OTHER to "Browser"
                                    )
                                ) {
                                    setState {
                                        this.newConditions = newConditions.set(index, con.copy(conditionValue = it))
                                    }
                                }
                            }
                        }

                    }
                    div(flexRow) {
                        button("Add condition") {
                            setState {
                                newConditions += LinkCondition(CONDITION_TYPE_PLATFORM, "")
                            }
                        }
                    }
                    div(flexRow) {
                        button("Save") {
                            submitFunction()
                        }
                        button("Cancel") {
                            setState { creatingNew = false }
                        }
                    }
                }
        }
    }
}

