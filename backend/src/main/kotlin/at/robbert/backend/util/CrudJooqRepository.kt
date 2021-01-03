package at.robbert.backend.util

import org.jooq.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class MappingCrudJooqRepository<R : UpdatableRecord<*>, M : Any?>(
    ctx: DSLContext,
    table: Table<R>,
    val mapper: RecordMapper<Record, M>
) : CrudJooqRepository<R>(ctx, table) {

    open fun <T> findByIdMapped(id: T): Mono<M> {
        return findById(id).map(mapper::map)
    }

    open fun insertMapped(record: R): Mono<M> {
        return insert(record).map(mapper::map)
    }

    open fun upsertMapped(record: R): Mono<M> {
        return upsert(record).map(mapper::map)
    }

}

abstract class CrudJooqRepository<R : UpdatableRecord<*>>(val ctx: DSLContext, val table: Table<R>) {

    open fun deleteAll(): Mono<Int> {
        return ctx.deleteFrom(table)
            .executeReactive()
    }

    open fun insert(record: R): Mono<R> {
        return ctx.insertInto(table)
            .set(record)
            .returning()
            .executeReturningOneReactive()
    }

    open fun count(): Mono<Int> {
        return ctx.selectCount().from(table).fetchOneReactive().map { it.component1()!! }
    }

    open fun upsert(record: R): Mono<R> {
        return ctx.insertInto(table)
            .set(record)
            .onConflict(table.primaryKey.fields).doUpdate()
            .set(record)
            .returning()
            .executeReturningOneReactive()
    }

    open fun selectAll(): Flux<R> {
        return ctx.selectFrom(table).fetchReactive()
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T> findById(id: T): Mono<R> {
        require(table.primaryKey.fields.size == 1)

        val pk: Field<T> = table.primaryKey.fields.single() as Field<T>

        return ctx.selectFrom(table)
            .where(pk.eq(id))
            .fetchOneReactive()
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T> deleteById(id: T): Mono<Int> {
        require(table.primaryKey.fields.size == 1)
        val pk: Field<T> = table.primaryKey.fields.single() as Field<T>

        return ctx.deleteFrom(table)
            .where(pk.eq(id))
            .executeReactive()
    }
}
