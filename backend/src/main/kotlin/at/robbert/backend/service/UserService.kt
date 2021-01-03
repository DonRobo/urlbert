package at.robbert.backend.service

import at.robbert.backend.jooq.Tables.LOGIN_USER
import at.robbert.backend.jooq.tables.records.LoginUserRecord
import at.robbert.backend.util.executeReactive
import at.robbert.backend.util.log
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.DSLContext
import org.springframework.data.annotation.Id
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

class UserRepositoryCustomImpl(private val ctx: DSLContext) : UserRepositoryCustom {
    private val lu = LOGIN_USER.`as`("lu")

    override fun createUser(user: User): Mono<Int> {
        return ctx.insertInto(lu)
            .set(LoginUserRecord().apply {
                this.username = user.username
                this.password = user.password
                this.secret = user.secret
            })
            .executeReactive()
    }

    override fun updatePasswordAndResetSecret(username: String, password: String): Mono<Int> {
        return ctx.update(lu)
            .set(lu.PASSWORD, password)
            .setNull(lu.SECRET)
            .where(lu.USERNAME.eq(username))
            .executeReactive()
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
        log.debug("Looking for $username")
        return userRepository.findById(username).map {
            val u = SpringUser(
                it.username,
                it.password,
                listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
            )
            log.debug("Found $u")
            u
        }
    }

}
