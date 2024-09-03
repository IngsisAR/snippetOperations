package austral.ingsisAR.snippetOperations.user.controller

import austral.ingsisAR.snippetOperations.user.model.dto.PaginatedUsersDTO
import austral.ingsisAR.snippetOperations.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users")
@Validated
class UserController(
    @Autowired
    private val userService: UserService,
) {
    @GetMapping
    fun getUsers(
        @RequestParam("page_number", defaultValue = "0") pageNumber: Int,
        @RequestParam("page_size", defaultValue = "10") pageSize: Int,
        @RequestParam("name", defaultValue = "") name: String,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<PaginatedUsersDTO> {
        return ResponseEntity.ok(userService.getUsers(jwt.subject, pageNumber, pageSize, name))
    }
}
