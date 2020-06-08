package at.robbert.backend

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class WebSecurityConfig {
    @Bean
    fun securitygWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        println("RUNNING2")
        return http.authorizeExchange()
            .pathMatchers(HttpMethod.GET, "/link/**").permitAll()
            .anyExchange().hasRole("ADMIN")
            .and().httpBasic()
            .and().build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(4)
    }

    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        println("RUNNING")
        val user = User
            .withUsername("robert")
            .password(passwordEncoder().encode("testtest1"))
            .roles("ADMIN")
            .build()
        return MapReactiveUserDetailsService(user)
    }
}
