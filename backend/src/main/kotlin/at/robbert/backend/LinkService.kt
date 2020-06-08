package at.robbert.backend

import at.robbert.redirector.data.Link
import at.robbert.redirector.data.LinkCondition
import at.robbert.redirector.data.MultiLink
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux

class CustomLinkRepositoryImpl(private val databaseClient: DatabaseClient) : CustomLinkRepository {
    override fun retrieveAllDescByAge(): Flux<MultiLink> {
        return databaseClient.select()
            .from(MultiLink::class.java).orderBy(desc("createdAt"))
            .fetch().all()
    }

}

interface CustomLinkRepository {
    fun retrieveAllDescByAge(): Flux<MultiLink>
}

interface LinkRepository : ReactiveCrudRepository<MultiLink, String>, CustomLinkRepository

@Service
class LinkService(private val linkRepository: LinkRepository) {
    suspend fun retrieveMultiLink(linkName: String): MultiLink {
        return linkRepository.findById(linkName).awaitSingle()
    }

    suspend fun retrieveLink(linkName: String, platform: String): Link {
        return retrieveMultiLink(linkName).links.sortedByDescending { it.conditions.size }.firstOrNull { link ->
            link.conditions.all { condition ->
                condition.conditionFulfilled(platform)
            }
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No link found")
    }

    suspend fun retrieveLinks(): List<MultiLink> {
        return linkRepository.retrieveAllDescByAge().collectList().awaitSingle()
    }

    suspend fun addOrUpdateLink(multiLink: MultiLink): MultiLink {
        return linkRepository.save(multiLink.copy(createdAt = null)).awaitSingle()
    }
}

private fun LinkCondition.conditionFulfilled(platform: String): Boolean {
    return when (conditionType) {
        "PLATFORM" -> conditionValue == platform
        else -> throw UnsupportedOperationException("Condition type $conditionType not supported")
    }
}
