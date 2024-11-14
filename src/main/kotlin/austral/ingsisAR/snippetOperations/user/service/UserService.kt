package austral.ingsisAR.snippetOperations.user.service

import austral.ingsisAR.snippetOperations.integration.Auth0ManagementService
import austral.ingsisAR.snippetOperations.user.model.dto.UsersDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired
    private val auth0ManagementService: Auth0ManagementService,
) {
    fun getUsers(
        userId: String,
        name: String,
    ): UsersDTO {
        val users = auth0ManagementService.getUsers(name)
        if (users.hasBody()) {
            return UsersDTO(users.body!!.filter { it.user_id != userId }, users.body!!.size)
        }
        return UsersDTO(listOf(), 0)
    }

    fun getUserById(userId: String): String {
        return auth0ManagementService.getUserById(userId).body!!.nickname
    }
}
