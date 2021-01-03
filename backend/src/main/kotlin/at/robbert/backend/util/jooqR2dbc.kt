package at.robbert.backend.util

import gofabian.r2dbc.jooq.ReactiveJooq
import org.jooq.Query
import org.jooq.Record
import org.jooq.Select
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <R : Record> Select<R>.fetchReactive(): Flux<R> {
    return ReactiveJooq.fetch(this)
}

fun Query.executeReactive(): Mono<Int> {
    return ReactiveJooq.execute(this)
}
