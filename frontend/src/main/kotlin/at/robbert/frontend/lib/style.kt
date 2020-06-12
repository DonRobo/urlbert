package at.robbert.frontend.lib

import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.borderRight
import kotlinx.css.properties.boxShadow
import kotlinx.html.DIV
import react.RBuilder
import react.dom.RDOMBuilder
import react.dom.div
import styled.injectGlobal

private val buttonColor = Color("#82D3FF")
private val buttonTextColor = Color.black
private val buttonHighlightColor = Color("#B7EAFF")

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
    flexCenter({
        display = Display.flex
        alignItems = kotlinx.css.Align.center
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
        backgroundColor = buttonColor
        fontWeight = FontWeight.bold
        padding(0.3.rem)
        margin(0.3.rem)
        borderRadius = 0.1.rem
        color = buttonTextColor
        hover {
            backgroundColor = buttonHighlightColor
        }
    }),
    buttonLeft({
        borderRight(
            1.px,
            BorderStyle.solid,
            Color.black
        )
        fontSize = 1.4.rem
        fontWeight = FontWeight.bold
        padding(0.3.rem)
        backgroundColor = buttonColor
        display = Display.flex
        alignItems = Align.center
        borderBottomLeftRadius = 0.3.rem
        borderTopLeftRadius = 0.3.rem
        cursor = Cursor.pointer
        color = buttonTextColor
        hover {
            backgroundColor = buttonHighlightColor
        }
    }),
    shadowedBox({
        boxShadow(Color.lightGray, spreadRadius = 0.2.rem, blurRadius = 0.2.rem)
        padding(1.rem)
        margin(1.rem)
    }),
    warning({
        color = Color.red
        fontWeight = FontWeight.bold
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
        "a" {
            color = Color.black
            textDecoration = TextDecoration.none
        }
        Styles.values().forEach {
            rule(".${it.name}", it.rule)
        }
    }
}
