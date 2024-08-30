package austral.ingsisAHRE.snippetOperations.snippet.model.dto

import jakarta.validation.constraints.NotBlank

data class ShareSnippetDTO(
    @field:NotBlank(message = "SnippetId is required")
    val snippetId: String,
    @field:NotBlank(message = "UserId is required")
    val userId: String,
)
