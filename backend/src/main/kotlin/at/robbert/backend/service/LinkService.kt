package at.robbert.backend.service

import at.robbert.backend.jooq.Tables
import at.robbert.backend.jooq.Tables.LINK_CLICK
import at.robbert.backend.jooq.tables.records.MultiLinkRecord
import at.robbert.backend.util.*
import at.robbert.redirector.data.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

class MultilinkMapper(
    private val ml: at.robbert.backend.jooq.tables.MultiLink,
    private val lc: at.robbert.backend.jooq.tables.LinkClick,
    private val objectMapper: ObjectMapper
) : FRecordMapper<MultiLink>() {

    private val allTime = select(count(lc.ID))
        .from(lc)
        .where(lc.MULTI_LINK_NAME.eq(ml.NAME))
        .asField<Int>("allTime")

    private val last24h = select(count(lc.ID))
        .from(lc)
        .where(lc.MULTI_LINK_NAME.eq(ml.NAME))
        .and(lc.TIME.youngerThan(Duration.ofHours(24)))
        .asField<Int>("last24h")

    override val fields = listOf(ml.NAME, ml.LINKS, ml.CREATED_AT, allTime, last24h)

    override fun mapData(r: LightRecord): MultiLink {
        return MultiLink(
            name = r[ml.NAME]!!,
            links = objectMapper.readValue(r[ml.LINKS]!!.data()),
            createdAt = r[ml.CREATED_AT]!!,
            stats = ClickCounts(r[allTime]!!, r[last24h]!!)
        )
    }
}

@Repository
class LinkRepository(
    ctx: DSLContext,
    private val objectMapper: ObjectMapper
) : CrudJooqRepository<MultiLinkRecord>(ctx, Tables.MULTI_LINK) {
    private val ml = Tables.MULTI_LINK
    private val lc = Tables.LINK_CLICK

    private val mapper = MultilinkMapper(ml, lc, objectMapper)

    fun retrieveAllDescByAge(): Flux<MultiLink> {
        return ctx.select(mapper.fields)
            .from(ml)
            .orderBy(ml.CREATED_AT)
            .fetchReactive()
            .map(mapper)
    }

    fun update(record: MultiLinkRecord): Mono<MultiLink> {
        return ctx.update(ml)
            .set(record)
            .where(ml.NAME.eq(record.name))
            .returning()
            .executeReturningOneReactive()
            .map(mapper)
    }

    override fun <T> deleteById(id: T): Mono<Int> {
        error("Use deleteLinkById($id)")
    }

    fun deleteLinkById(id: String): Mono<Int> {
        return ctx.deleteFrom(lc).where(lc.MULTI_LINK_NAME.eq(id)).executeReactive()
            .then(ctx.deleteFrom(ml).where(ml.NAME.eq(id)).executeReactive())
    }

    fun findByIdMapped(linkName: String): Flux<MultiLink> {
        return ctx.select(mapper.fields)
            .from(ml)
            .where(ml.NAME.eq(linkName))
            .fetchReactive()
            .map(mapper)
    }

    fun insertMapped(r: MultiLinkRecord): Mono<MultiLink> {
        return ctx.insertInto(ml)
            .set(r)
            .returning()
            .executeReturningOneReactive()
            .map(mapper)
    }
}

@Service
class LinkService(
    private val linkRepository: LinkRepository,
    private val objectMapper: ObjectMapper,
    val ctx: DSLContext
) {
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
        linkRepository.deleteLinkById(name).awaitFirstOrNull()
    }

    suspend fun linkClicked(linkName: String, redirectedTo: String): Int {
        val lc = LINK_CLICK
        return ctx.insertInto(lc)
            .set(lc.MULTI_LINK_NAME, linkName)
            .set(lc.REDIRECTED_TO, redirectedTo)
            .executeReactive()
            .awaitSingle()
    }
}

private fun LinkCondition.conditionFulfilled(platform: String, country: String): Boolean {
    return when (conditionType) {
        CONDITION_TYPE_PLATFORM -> conditionValue == platform
        CONDITION_TYPE_COUNTRY -> conditionValue.equals(country, ignoreCase = true)
        else -> throw UnsupportedOperationException("Condition type $conditionType not supported")
    }
}
