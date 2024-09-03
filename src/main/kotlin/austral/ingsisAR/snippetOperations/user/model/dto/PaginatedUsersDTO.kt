package austral.ingsisAR.snippetOperations.user.model.dto

data class PaginatedUsersDTO(
    val users: List<UserDTO>,
    val total: Int,
)
