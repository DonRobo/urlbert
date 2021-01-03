package at.robbert.backend.util

import org.jooq.Field
import org.jooq.Record
import org.jooq.RecordMapper

abstract class FRecordMapper<E : Any?> : RecordMapper<Record, E>, (Record) -> E {
    abstract val fields: List<Field<out Any>>

    final override fun map(record: Record): E {
        return mapData(DelegatingLightRecord(record))
    }

    abstract fun mapData(r: LightRecord): E

    override fun invoke(r: Record): E = map(r)
}

interface LightRecord {
    operator fun <T> get(field: Field<T>): T?
}

class DelegatingLightRecord(private val record: Record) : LightRecord {
    override operator fun <T> get(field: Field<T>): T {
        return record[field]
    }
}
