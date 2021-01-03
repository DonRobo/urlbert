package at.robbert.backend.service

import at.robbert.backend.jooq.Tables
import at.robbert.backend.jooq.tables.records.MultiLinkRecord
import at.robbert.backend.util.*
import at.robbert.redirector.data.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class MultilinkMapper(
    private val ml: at.robbert.backend.jooq.tables.MultiLink,
    private val objectMapper: ObjectMapper
) : RecordMapper<Record, MultiLink> {
    override fun map(r: Record): MultiLink = MultiLink(
        name = r[ml.NAME],
        links = objectMapper.readValue(r[ml.LINKS].data()),
        createdAt = r[ml.CREATED_AT]
    )
}

@Repository
class LinkRepository(
    ctx: DSLContext,
    private val objectMapper: ObjectMapper
) : MappingCrudJooqRepository<MultiLinkRecord, MultiLink>(ctx, Tables.MULTI_LINK, MultilinkMapper(ml, objectMapper)) {
    companion object {
        private val ml = Tables.MULTI_LINK
    }

    fun retrieveAllDescByAge(): Flux<MultiLink> {
        return ctx.selectFrom(ml)
            .orderBy(ml.CREATED_AT)
            .fetchReactive()
            .map(mapper::map)
    }

    fun update(record: MultiLinkRecord): Mono<MultiLink> {
        return ctx.update(ml)
            .set(record)
            .where(ml.NAME.eq(record.name))
            .returning()
            .executeReturningOneReactive()
            .map(mapper::map)
    }
}

@Service
class LinkService(private val linkRepository: LinkRepository, private val objectMapper: ObjectMapper) {
    suspend fun retrieveMultiLink(linkName: String): MultiLink? {
        return linkRepository.findByIdMapped(linkName).awaitFirstOrNull()
    }

    suspend fun retrieveLink(linkName: String, platform: String, country: String): Link {
        return retrieveMultiLink(linkName)?.links?.sortedByDescending { it.conditions.size }?.firstOrNull { link ->
            link.conditions.all { condition ->
                condition.conditionFulfilled(platform, country)
            }
        } ?: notFound("No link found")
    }

    suspend fun retrieveLinks(): List<MultiLink> {
        return linkRepository.retrieveAllDescByAge().collectList().awaitSingle()
    }

    suspend fun updateLink(multiLink: MultiLink): MultiLink {
        return linkRepository.update(
            MultiLinkRecord().apply {
                name = multiLink.name
                links = objectMapper.toJsonB(multiLink.links)
            }
        ).awaitSingle()
    }

    suspend fun addLink(multiLink: MultiLink): MultiLink {
        return linkRepository.insertMapped(MultiLinkRecord().apply {
            this.name = multiLink.name
            this.links = objectMapper.toJsonB(multiLink.links)
        }).awaitSingle()
    }

    suspend fun deleteLink(name: String) {
        linkRepository.deleteById(name).awaitFirstOrNull()
    }
}

private fun LinkCondition.conditionFulfilled(platform: String, country: String): Boolean {
    return when (conditionType) {
        CONDITION_TYPE_PLATFORM -> conditionValue == platform
        CONDITION_TYPE_COUNTRY -> conditionValue.equals(country, ignoreCase = true)
        else -> throw UnsupportedOperationException("Condition type $conditionType not supported")
    }
}
