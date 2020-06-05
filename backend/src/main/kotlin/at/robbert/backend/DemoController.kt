package at.robbert.backend

import at.robbert.redirector.data.Test
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/")
class DemoController {

    @GetMapping("hello")
    fun test(): Mono<Test> = mono {
        Test("Server says hello!")
    }

}
