package at.robbert.backend.util

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun notFound(msg: String = "Not found"): Nothing = throw ResponseStatusException(HttpStatus.NOT_FOUND, msg)
