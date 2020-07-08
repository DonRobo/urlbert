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
    var redirection: Redirection
}

external interface CreateLinkProps : RProps {
    var createLink: (Link) -> Job
    var cancel: () -> Unit
}

class CreateLinkComponent : RComponent<CreateLinkProps, CreateLinkState>() {
    init {
        state.newConditions = emptyList()
        state.newLink = ""
        state.redirection = Redirection(RedirectMethod.HTTP, 301)
    }

    override fun RBuilder.render() {
        div(Styles.flexColumn, Styles.ml3) {
            val submitFunction = {
                GlobalScope.launch {
                    props.createLink(Link(state.newConditions.filter { it.isValid }, state.newLink, state.redirection))
                }
            }
            formInput("Link", state.newLink, { submitFunction() }, InputType.text) {
                setState {
                    newLink = it
                }
            }
            formSelect(
                "Redirection method",
                state.redirection.method.name,
                options = RedirectMethod.values().map {
                    it.name to when (it) {
                        RedirectMethod.HTTP -> "HTTP Redirect"
                        RedirectMethod.JS -> "Javascript Redirect"
                        RedirectMethod.FAST_JS -> "Lightweight Javascript Redirect"
                    }
                },
                emptyOption = false
            ) {
                val m = RedirectMethod.valueOf(it)
                setState {
                    if (m != RedirectMethod.HTTP && redirection.status != null) {
                        redirection = redirection.copy(status = null)
                    }
                    redirection = redirection.copy(method = m)
                }
            }
            if (state.redirection.method == RedirectMethod.HTTP) {
                formSelect(
                    "HTTP Status",
                    state.redirection.status?.toString() ?: "301",
                    options = listOf(
                        "301" to "301 Moved Permanently",
                        "302" to "302 Found",
                        "307" to "307 Temporary Redirect"
                    ),
                    emptyOption = false
                ) {
                    setState {
                        redirection = redirection.copy(status = it.toInt())
                    }
                }
            }
            state.newConditions.forEachIndexed { index, con ->
                div {
                    key = "condition#$index"
                    div(Styles.warning) {
                        if (!con.isValid) {
                            +"Invalid condition!"
                        }
                    }

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
                                listOf(
                                    CONDITION_TYPE_PLATFORM to "Platform",
                                    CONDITION_TYPE_COUNTRY to "Country"
                                ),
                                emptyOption = false
                            ) {
                                this@CreateLinkComponent.setState {
                                    newConditions = newConditions.set(index, con.copy(conditionType = it))
                                }
                                console.log(con, it, index)
                            }
                            when (con.conditionType) {
                                CONDITION_TYPE_PLATFORM -> {
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
                                CONDITION_TYPE_COUNTRY -> {
                                    formInput(
                                        "Condition value", con.conditionValue
                                    ) {
                                        setState {
                                            this.newConditions =
                                                newConditions.set(index, con.copy(conditionValue = it.toUpperCase()))
                                        }
                                    }
                                }
                                else -> {
                                    +"Select condition type!"
                                }
                            }
                        }
                    }
                }
                Unit
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
                    if (state.newConditions.all { it.isValid } && state.newLink.isNotBlank()) {
                        submitFunction()
                    }
                }
                button("Cancel") {
                    props.cancel()
                }
            }
        }
    }

}
