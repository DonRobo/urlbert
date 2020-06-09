package at.robbert.frontend.components

import at.robbert.frontend.lib.Styles.flexColumn
import at.robbert.frontend.lib.Styles.flexNoStretch
import at.robbert.frontend.lib.button
import at.robbert.frontend.lib.div
import at.robbert.frontend.lib.formInput
import at.robbert.redirector.UserService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.Color
import kotlinx.css.color
import kotlinx.html.InputType
import react.*
import styled.css
import styled.styledDiv
import styled.styledIns

external interface UserComponentState : RState {
    var secret: String
    var password: String
    var repeatPassword: String
}

class UserComponent : RComponent<RProps, UserComponentState>() {
    init {
        state.secret = ""
        state.password = ""
        state.repeatPassword = ""
    }

    override fun RBuilder.render() {
        div(flexColumn, flexNoStretch) {
            fun submit() {
                GlobalScope.launch {
                    if (state.password == state.repeatPassword) {
                        UserService.updatePassword(state.password, state.secret)
                        setState {
                            secret = ""
                            password = ""
                            repeatPassword = ""
                        }
                    }
                }
            }
            formInput("Secret", state.secret) {
                setState {
                    secret = it
                }
            }
            formInput("Password", state.password, type = InputType.password) {
                setState {
                    password = it
                }
            }
            formInput("Repeat", state.repeatPassword, { submit() }, type = InputType.password) {
                setState {
                    repeatPassword = it
                }
            }
            if (state.password != state.repeatPassword) {
                styledIns {
                    css {
                        color = Color.red
                    }
                    +"Passwords don't match!"
                }
            }
            if (state.password.length in 1 until 6) {
                styledDiv {
                    css {
                        color = Color.red
                    }
                    +"Password too short!"
                }
            }
            if (!(state.password.length in 1 until 6 || state.password != state.repeatPassword || state.secret.isBlank()))
                button("Submit") {
                    submit()
                }
        }
    }

}
