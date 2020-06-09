package at.robbert.backend

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

typealias SpringUser = org.springframework.security.core.userdetails.User

@Table("login_user")
class User(@Id val username: String, val password: String)

class UserRepositoryCustomImpl(private val databaseClient: DatabaseClient) : UserRepositoryCustom {
    override fun createUser(user: User): Mono<Int> {
        return databaseClient.insert().into(User::class.java)
            .using(user).fetch().rowsUpdated()
    }
}

interface UserRepositoryCustom {
    fun createUser(user: User): Mono<Int>
}

interface UserRepository : ReactiveCrudRepository<User, String>, UserRepositoryCustom

@Service
class UserService(val userRepository: UserRepository) : ReactiveUserDetailsService {

    suspend fun createUser(username: String, password: String): User {
        userRepository.createUser(User(username, password)).awaitSingle()
        return getUser(username)
    }

    suspend fun getUser(username: String): User {
        return userRepository.findById(username).awaitSingle()
    }

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findById(username).map {
            SpringUser(
                it.username,
                it.password,
                listOf(SimpleGrantedAuthority("ADMIN"))
            )
        }
    }

}
