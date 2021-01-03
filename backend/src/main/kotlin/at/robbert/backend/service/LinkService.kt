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
import org.jooq.JSONB
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class CustomLinkRepositoryImpl(private val ctx: DSLContext, private val objectMapper: ObjectMapper) :
    CustomLinkRepository {
    private val ml = Tables.MULTI_LINK.`as`("ml")

    override fun retrieveAllDescByAge(): Flux<MultiLink> {
        return ctx.selectFrom(ml)
            .orderBy(ml.CREATED_AT)
            .fetchReactive()
            .map {
                val ml = MultiLink(
                    name = it.name,
                    links = objectMapper.readValue(it.links.data()),
                    createdAt = it.createdAt
                )
                log.debug(ml.toString())
                ml
            }
    }

    override fun insert(multiLink: MultiLink): Mono<Int> {
        return ctx.insertInto(ml)
            .set(MultiLinkRecord().apply {
                this.name = multiLink.name
                this.links = JSONB.valueOf(objectMapper.writeValueAsString(multiLink.links))
                this.createdAt = currentTimestamp()
            })
            .executeReactive()
    }
}

interface CustomLinkRepository {
    fun retrieveAllDescByAge(): Flux<MultiLink>
    fun insert(multiLink: MultiLink): Mono<Int>
}

interface LinkRepository : ReactiveCrudRepository<MultiLink, String>, CustomLinkRepository

@Service
class LinkService(private val linkRepository: LinkRepository) {
    suspend fun retrieveMultiLink(linkName: String): MultiLink? {
        return linkRepository.findById(linkName).awaitFirstOrNull()
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
        val ml = retrieveMultiLink(multiLink.name) ?: error("Updated link disappeared!")
        return linkRepository.save(ml.copy(links = multiLink.links)).awaitSingle()
    }

    suspend fun addLink(multiLink: MultiLink): MultiLink {
        linkRepository.insert(multiLink.copy(createdAt = null)).awaitSingle()
        return linkRepository.findById(multiLink.name).awaitSingle()
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
