package austral.ingsisAHRE.snippetOperations.user.model.dto

data class PaginatedUsersDTO(
    val users: List<UserDTO>,
    val total: Int,
)
