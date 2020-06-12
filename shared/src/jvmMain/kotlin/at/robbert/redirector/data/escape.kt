package at.robbert.redirector.data

import org.apache.commons.text.StringEscapeUtils

fun String.escapeJsString(): String {
    return StringEscapeUtils.escapeEcmaScript(this)
}
