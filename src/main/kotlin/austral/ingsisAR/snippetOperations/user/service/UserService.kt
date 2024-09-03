package austral.ingsisAR.snippetOperations.user.service

import austral.ingsisAR.snippetOperations.integration.Auth0Service
import austral.ingsisAR.snippetOperations.user.model.dto.PaginatedUsersDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired
    private val auth0Service: Auth0Service,
) {
    fun getUsers(
        userId: String,
        pageNumber: Int,
        pageSize: Int,
        name: String,
    ): PaginatedUsersDTO {
        val users = auth0Service.getUsers(pageNumber, pageSize, name)
        if (users.hasBody()) {
            return PaginatedUsersDTO(users.body!!.filter { it.user_id != userId }, users.body!!.size)
        }
        return PaginatedUsersDTO(listOf(), 0)
    }

    fun getUserById(userId: String): String {
        return auth0Service.getUserById(userId).body!!.nickname
    }
}
