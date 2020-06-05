package at.robbert.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LinkApplication

fun main(args: Array<String>) {
    runApplication<LinkApplication>(*args)
}
