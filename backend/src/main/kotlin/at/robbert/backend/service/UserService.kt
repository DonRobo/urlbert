package at.robbert.backend.service

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

typealias SpringUser = org.springframework.security.core.userdetails.User

@Table("login_user")
class User(@Id val username: String, val password: String, val secret: UUID?)

class UserRepositoryCustomImpl(private val databaseClient: DatabaseClient) : UserRepositoryCustom {
    override fun createUser(user: User): Mono<Int> {
        return databaseClient.insert().into(User::class.java)
            .using(user).fetch().rowsUpdated()
    }

    override fun updatePasswordAndResetSecret(username: String, password: String): Mono<Int> {
        return databaseClient
            .update()
            .table(User::class.java)
            .using(User(username, password, null))
            .fetch().rowsUpdated()
    }
}

interface UserRepositoryCustom {
    fun createUser(user: User): Mono<Int>
    fun updatePasswordAndResetSecret(username: String, password: String): Mono<Int>
}

interface UserRepository : ReactiveCrudRepository<User, String>, UserRepositoryCustom {
    fun findUserBySecret(secret: UUID): Mono<User>
}

@Service
class UserService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) :
    ReactiveUserDetailsService {

    suspend fun createUser(username: String, password: String): User {
        userRepository.createUser(User(username, password, null)).awaitSingle()
        return getUser(username)
    }

    suspend fun createUser(username: String, secret: UUID): User {
        userRepository.createUser(User(username, "tmp", secret)).awaitSingle()
        return getUser(username)
    }

    suspend fun updatePassword(password: String, secret: UUID): User {
        val user = userRepository.findUserBySecret(secret).awaitSingle()
        if (user.secret == secret) {
            userRepository.updatePasswordAndResetSecret(user.username, passwordEncoder.encode(password)).awaitSingle()
            return getUser(user.username)
        } else {
            error("User not found")
        }
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
