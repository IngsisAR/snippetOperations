package austral.ingsisAHRE.snippetOperations.integration

data class GetPaginatedSnippetPermissionsDTO(
    val permissions: List<GetSnippetPermissionsDTO>,
    val total: Int,
)

data class GetSnippetPermissionsDTO(
    val id: String,
    val snippetId: String,
    val authorId: String,
)
