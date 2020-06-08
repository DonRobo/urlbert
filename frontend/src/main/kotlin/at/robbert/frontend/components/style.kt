package at.robbert.frontend.components

import kotlinx.css.*
import kotlinx.css.properties.borderRight
import kotlinx.css.properties.boxShadow
import kotlinx.html.DIV
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.div
import styled.injectGlobal

@Suppress("EnumEntryName")
enum class Styles(val rule: RuleSet) {
    flexColumn({
        display = Display.flex
        flexDirection = FlexDirection.column
    }),
    flexRow({
        display = Display.flex
        flexDirection = FlexDirection.row
    }),
    flexStretch({
        display = Display.flex
        alignItems = Align.stretch
    }),
    flexNoStretch({
        display = Display.flex
        alignItems = Align.flexStart
    }),
    mpMedium({
        margin(0.5.rem)
        padding(0.5.rem)
    }),
    pl1({
        paddingLeft = 1.rem
    }),
    ml3({
        marginLeft = 3.rem
    }),
    button({
        cursor = Cursor.pointer
        backgroundColor = Color.lightGray
        padding(0.3.rem)
        margin(0.3.rem)
        borderRadius = 0.3.rem

        hover {
            backgroundColor = Color("#E0E0E0")
        }
    }),
    buttonLeft({
        borderRight(
            1.px,
            BorderStyle.solid,
            Color.black
        )
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
    }),
    shadowedBox({
        boxShadow(Color.lightGray, spreadRadius = 0.2.rem, blurRadius = 0.2.rem)
        padding(1.rem)
        margin(1.rem)
    })
}

fun RBuilder.div(vararg styles: Styles, block: RDOMBuilder<DIV>.() -> Unit) =
    this.div(styles.joinToString(" ") { it.name }, block)

fun initializeCss() {
    injectGlobal {
        "label"{
            display = Display.flex
            flexDirection = FlexDirection.column
        }
        Styles.values().forEach {
            rule(".${it.name}", it.rule)
        }
    }
}
