package at.robbert.frontend.components

import kotlinx.html.DIV
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyDownFunction
import org.w3c.dom.events.MouseEvent
import react.dom.*

fun RDOMBuilder<DIV>.button(label: String, block: (MouseEvent) -> Unit) = button(Styles.button, label, block)
fun RDOMBuilder<DIV>.button(style: Styles, label: String, block: (MouseEvent) -> Unit) =
    div(style) {
        attrs {
            onClickFunction = {
                block(it.asDynamic())
            }
        }
        +label
    }

fun <T> RDOMBuilder<DIV>.formInput(
    name: String,
    value: T,
    onSubmit: () -> Unit,
    type: InputType,
    onChange: (T) -> Unit
) {
    label {
        +name
        input(type) {
            attrs {
                onChangeFunction = {
                    val newValue: T = when (type) {
                        InputType.text -> it.target.asDynamic().value
                        else -> throw UnsupportedOperationException("Input type $type not supported")
                    } as T
                    onChange(newValue)
                }
                onKeyDownFunction = {
                    val pressedKey = it.asDynamic().key as String
                    if (pressedKey == "Enter") {
                        onSubmit()
                    }
                }
                this.value = value.toString()
            }
        }
    }
}

fun RDOMBuilder<DIV>.formSelect(
    name: String,
    value: String,
    options: List<Pair<String, String>>,
    emptyOption: Boolean = options.size != 1,
    onChange: (String) -> Unit
) {
    label {
        +name
        select {
            attrs {
                set("value", value)

                onChangeFunction = {
                    onChange(it.target.asDynamic().value as String)
                }
            }
            if (emptyOption)
                option {
                    attrs {
                        this.value = ""
                    }
                    +""
                }
            options.forEach { (value, label) ->
                option {
                    attrs {
                        this.value = value
                    }
                    +label
                }
            }
        }
    }
}
