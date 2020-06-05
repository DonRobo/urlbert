package at.robbert.backend

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service

interface LinkRepository : ReactiveCrudRepository<MultiLink, String>

@Service
class LinkService(private val linkRepository: LinkRepository) {
    suspend fun retrieveMultiLink(linkName: String): MultiLink {
        return linkRepository.findById(linkName).awaitSingle()
    }

    suspend fun retrieveLink(linkName: String, platform: String): Link {
        return retrieveMultiLink(linkName).links.sortedByDescending { it.conditions.size }.first { link ->
            link.conditions.all { condition ->
                condition.conditionFulfilled(platform)
            }
        }
    }
}

private fun LinkCondition.conditionFulfilled(platform: String): Boolean {
    return when (conditionType) {
        "PLATFORM" -> conditionValue == platform
        else -> throw UnsupportedOperationException("Conditiontype $conditionType not supported")
    }
}
