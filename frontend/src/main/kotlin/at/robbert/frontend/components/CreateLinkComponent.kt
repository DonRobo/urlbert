package at.robbert.frontend.components

import at.robbert.frontend.lib.*
import at.robbert.redirector.data.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import react.*

fun RBuilder.createLink(handler: CreateLinkProps.() -> Unit): ReactElement {
    return child(CreateLinkComponent::class) {
        this.attrs(handler)
    }
}


external interface CreateLinkState : RState {
    var newConditions: List<LinkCondition>
    var newLink: String
}

external interface CreateLinkProps : RProps {
    var createLink: (Link) -> Job
    var cancel: () -> Unit
}

class CreateLinkComponent : RComponent<CreateLinkProps, CreateLinkState>() {
    init {
        state.newConditions = emptyList()
        state.newLink = ""
    }

    override fun RBuilder.render() {
        div(Styles.flexColumn, Styles.ml3) {
            val submitFunction = {
                GlobalScope.launch {
                    props.createLink(Link(state.newConditions.filter { it.isValid }, state.newLink))
                }
            }
            formInput("Link", state.newLink, { submitFunction() }, InputType.text) {
                setState {
                    newLink = it
                }
            }
            state.newConditions.forEachIndexed { index, con ->
                div(Styles.flexRow, Styles.mpMedium) {
                    button(Styles.buttonLeft, "x") {
                        setState {
                            this.newConditions = this.newConditions.minusIndex(index)
                        }
                    }
                    div(Styles.flexColumn, Styles.pl1) {
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
            div(Styles.flexRow) {
                button("Add condition") {
                    setState {
                        newConditions += LinkCondition(CONDITION_TYPE_PLATFORM, "")
                    }
                }
            }
            div(Styles.flexRow) {
                button("Save") {
                    submitFunction()
                }
                button("Cancel") {
                    props.cancel()
                }
            }
        }
    }

}
