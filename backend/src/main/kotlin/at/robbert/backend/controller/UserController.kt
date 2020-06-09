package at.robbert.backend.controller

import at.robbert.backend.service.UserService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

data class UpdatePasswordPayload(val password: String, val secret: UUID)

@RestController
class UserController(val userService: UserService) {

    @PutMapping("/api/user/{user}/password")
    suspend fun updatePassword(@PathVariable user: String, @RequestBody update: UpdatePasswordPayload): Boolean {
        return userService.updatePassword(user, update.password, update.secret).username == user
    }
}
