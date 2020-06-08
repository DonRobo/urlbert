package at.robbert.frontend.components

import at.robbert.frontend.TELL_ROBERT
import at.robbert.redirector.data.*
import react.*
import react.dom.div

fun RBuilder.condition(handler: ConditionProps.() -> Unit): ReactElement {
    return child(ConditionComponent::class) {
        this.attrs(handler)
    }
}

external interface ConditionProps : RProps {
    var condition: LinkCondition
}

class ConditionComponent : RComponent<ConditionProps, RState>() {
    override fun RBuilder.render() {
        div {
            +when (props.condition.conditionType) {
                CONDITION_TYPE_COUNTRY -> "Only accessible from country ${props.condition.conditionValue}"
                CONDITION_TYPE_PLATFORM -> "Only accessible from " + when (props.condition.conditionValue) {
                    PLATFORM_ANDROID -> "Android devices"
                    PLATFORM_IOS -> "iOS devices"
                    PLATFORM_OTHER -> "PCs"
                    else -> "Unknown platform(${props.condition.conditionValue} $TELL_ROBERT"
                }
                else -> "Unsupported condition(${props.condition.conditionType}) $TELL_ROBERT"
            }
        }
    }

}
