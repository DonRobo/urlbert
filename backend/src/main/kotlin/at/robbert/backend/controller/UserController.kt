package at.robbert.backend.controller

import at.robbert.backend.service.UserService
import at.robbert.redirector.data.UpdatePasswordPayload
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController(val userService: UserService) {

    @PutMapping("/api/user/setPassword")
    suspend fun updatePassword(@RequestBody update: UpdatePasswordPayload): Boolean {
        userService.updatePassword(update.password, update.secret)
        return true
    }
}
