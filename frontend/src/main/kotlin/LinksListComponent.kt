import at.robbert.redirector.LinkService
import at.robbert.redirector.data.LinkCondition
import at.robbert.redirector.data.MultiLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.properties.border
import kotlinx.css.properties.boxShadow
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
}

external interface ConditionProps : RProps {
    var condition: LinkCondition
}

class ConditionComponent : RComponent<ConditionProps, RState>() {
    override fun RBuilder.render() {
        div {
            +"Condition: ${props.condition.conditionType}: ${props.condition.conditionValue}"
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
            props.multiLink.links.sortedByDescending { it.conditions.size }.forEach { link ->
                styledDiv {
                    css {
                        margin(0.5.rem)
                        padding(0.5.rem)
                        border(1.px, BorderStyle.solid, Color.gray, 3.px)
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

external interface LinksState : RState {
    var multiLinks: List<MultiLink>?
}

class LinksListComponent : RComponent<RProps, LinksState>() {
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
                    }
                }
            }
        }
    }
}
