package austral.ingsisAR.snippetOperations.snippet.model.dto

import jakarta.validation.constraints.NotBlank

data class UpdateSnippetDTO(
    @field:NotBlank(message = "Content is required")
    val content: String,
)
