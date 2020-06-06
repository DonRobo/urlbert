import at.robbert.redirector.LinkService
import at.robbert.redirector.data.*
import kotlinx.coroutines.*
import kotlinx.css.*
import kotlinx.css.properties.borderRight
import kotlinx.css.properties.boxShadow
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.ImplicitReflectionSerializer
import react.*
import react.dom.a
import react.dom.div
import styled.css
import styled.styledDiv
import kotlin.browser.window

fun RBuilder.linkList(handler: RProps.() -> Unit = {}): ReactElement {
    return child(LinksListComponent::class) {
        this.attrs(handler)
    }
}

fun RBuilder.multiLink(handler: MultiLinkProps.() -> Unit): ReactElement {
    return child(MultiLinkComponent::class) {
        this.attrs(handler)
    }
}

fun RBuilder.condition(handler: ConditionProps.() -> Unit): ReactElement {
    return child(ConditionComponent::class) {
        this.attrs(handler)
    }
}

external interface MultiLinkProps : RProps {
    var multiLink: MultiLink
    var deleteLink: (Int) -> Job
}

external interface ConditionProps : RProps {
    var condition: LinkCondition
}

class ConditionComponent : RComponent<ConditionProps, RState>() {
    override fun RBuilder.render() {
        div {
            +when (props.condition.conditionType) {
                CONDITION_TYPE_COUNTRY -> "Only accessible from country ${props.condition.conditionValue}"
                CONDITION_TYPE_PLATFORM -> "Only accessible from " + when (props.condition.conditionValue) {
                    PLATFORM_ANDROID -> "Android devices"
                    PLATFORM_IOS -> "iOS devices"
                    PLATFORM_OTHER -> "PCs"
                    else -> "Unknown platform(${props.condition.conditionValue} $TELL_ROBERT"
                }
                else -> "Unsupported condition(${props.condition.conditionType}) $TELL_ROBERT"
            }
        }
    }

}

class MultiLinkComponent : RComponent<MultiLinkProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                boxShadow(Color.lightGray, spreadRadius = 0.2.rem, blurRadius = 0.2.rem)
                padding(1.rem)
                margin(1.rem)
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
            props.multiLink.links.mapIndexed { index, link -> link to index }
                .sortedByDescending { it.first.conditions.size }.forEach { (link, index) ->
                    styledDiv {
                        css {
                            margin(0.5.rem)
                            padding(0.5.rem)
//                        border(1.px, BorderStyle.solid, Color.gray, 3.px)
                            display = Display.flex
                            alignItems = Align.stretch
                        }
                        styledDiv {
                            css {
                                borderRight(1.px, BorderStyle.solid, Color.black)
                                padding(0.3.rem)
                                backgroundColor = Color.lightGray
                                display = Display.flex
                                alignItems = Align.center
                                borderBottomLeftRadius = 0.3.rem
                                borderTopLeftRadius = 0.3.rem
                                cursor = Cursor.pointer
                                hover {
                                    backgroundColor = Color("#E0E0E0")
                                }
                            }
                            attrs {
                                onClickFunction = {
                                    props.deleteLink(index)
                                }
                            }
                            div {
                                +"x"
                            }
                        }
                        styledDiv {
                            css {
                                paddingLeft = 1.rem
                            }
                            a(link.url) {
                                +link.url
                            }
                            link.conditions.forEach {
                                condition {
                                    condition = it
                                }
                            }
                        }
                }
            }
        }
    }

}

external interface LinksState : RState {
    var multiLinks: List<MultiLink>?
}

class LinksListComponent : RComponent<RProps, LinksState>() {
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
