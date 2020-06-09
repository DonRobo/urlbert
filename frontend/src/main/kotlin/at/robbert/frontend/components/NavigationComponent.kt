package at.robbert.frontend.components

import at.robbert.frontend.lib.Styles.flexColumn
import at.robbert.frontend.lib.Styles.flexRow
import at.robbert.frontend.lib.div
import react.*
import react.dom.a
import react.router.dom.hashRouter
import react.router.dom.route
import react.router.dom.switch

fun RBuilder.navigation(handler: RProps.() -> Unit = {}): ReactElement {
    return child(NavigationComponent::class) {
        this.attrs(handler)
    }
}

class NavigationComponent : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        div(flexColumn) {
            div(flexRow) {
                a(href = "#/", classes = "button") {
                    +"Main"
                }
                a(href = "#/user", classes = "button") {
                    +"User"
                }
            }
        }
        div {
            hashRouter {
                switch {
                    route("/", LinksListComponent::class, exact = true)
                    route("/user", UserComponent::class, exact = true)
                }
            }
        }
    }

}
