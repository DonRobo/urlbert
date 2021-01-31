package at.robbert.frontend.components

import at.robbert.frontend.lib.Styles
import at.robbert.frontend.lib.button
import at.robbert.frontend.lib.div
import at.robbert.redirector.data.Link
import at.robbert.redirector.data.RedirectMethod.*
import kotlinx.coroutines.Job
import react.*
import react.dom.a
import react.dom.div

fun RBuilder.displayLink(handler: LinkProps.() -> Unit): ReactElement {
    return child(LinkComponent::class) {
        this.attrs(handler)
    }
}

external interface LinkProps : RProps {
    var deleteLink: () -> Job
    var link: Link
}

class LinkComponent : RComponent<LinkProps, RState>() {
    override fun RBuilder.render() {
        div(Styles.flexRow, Styles.flexStretch, Styles.mpMedium) {
            button(Styles.buttonLeft, "x") {
                props.deleteLink()
            }
            div(Styles.pl1) {
                a(props.link.url) {
                    +props.link.url
                }
                div {
                    +when (props.link.redirection.method) {
                        HTTP -> "HTTP ${props.link.redirection.status ?: "NOT_SET"} redirect"
                        JS -> "Redirect using Javascript"
                        FAST_JS -> "Redirect using lightweight Javascript"
                    }
                }
                if (props.link.conditions.isNotEmpty()) {
                    props.link.conditions.forEachIndexed { i, con ->
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
    }
}
