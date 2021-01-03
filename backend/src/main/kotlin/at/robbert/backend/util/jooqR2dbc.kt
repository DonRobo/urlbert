package at.robbert.backend.util

import gofabian.r2dbc.jooq.ReactiveJooq
import org.jooq.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <R : Record> Select<R>.fetchReactive(): Flux<R> {
    return ReactiveJooq.fetch(this)
}

fun <R : Record> Select<R>.fetchOneReactive(): Mono<R> {
    return ReactiveJooq.fetchOne(this)
}

fun Query.executeReactive(): Mono<Int> {
    return ReactiveJooq.execute(this)
}

fun <R : Record> InsertResultStep<R>.executeReturningOneReactive(): Mono<R> {
    return ReactiveJooq.executeReturningOne(this)
}

fun <R : Record> UpdateResultStep<R>.executeReturningOneReactive(): Mono<R> {
    return ReactiveJooq.executeReturningOne(this)
}
