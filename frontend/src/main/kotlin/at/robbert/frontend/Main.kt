package at.robbert.frontend

import at.robbert.frontend.components.initializeCss
import at.robbert.frontend.components.linkList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import react.*
import react.dom.render
import styled.css
import styled.styledDiv
import kotlin.browser.document
import kotlin.coroutines.CoroutineContext

const val TELL_ROBERT = "(if you see this tell Robert)"

class ApplicationComponent : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                width = 100.pct
                height = 100.pct
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.center
            }
            styledDiv {
                css {
                    padding(1.rem)
                    margin(2.rem)
                    backgroundColor = Color("#F9F9F9")
                    borderRadius = 0.5.rem
                    width = 80.pct
                    minHeight = 80.pct
                    overflowY = Overflow.auto
                    overflowX = Overflow.hidden
                    boxShadow(Color.lightGray, spreadRadius = 1.rem, blurRadius = 5.rem)
                }
                linkList()
            }
        }
    }

}

private class Application : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job()

    fun start() {
        initializeCss()
        document.getElementById("root")?.let {
            render(buildElement {
                val el = child(ApplicationComponent::class) {

                }
            }, it)
        }
    }
}

fun main() {
    Application().start()
}
