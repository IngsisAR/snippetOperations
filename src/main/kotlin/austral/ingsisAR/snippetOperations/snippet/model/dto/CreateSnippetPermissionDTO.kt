package austral.ingsisAR.snippetOperations.snippet.model.dto

data class CreateSnippetPermissionDTO(
    val snippetId: String,
    val userId: String,
    val permissionType: String,
)
