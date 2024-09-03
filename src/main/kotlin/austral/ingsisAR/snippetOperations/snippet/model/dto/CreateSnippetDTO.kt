package austral.ingsisAR.snippetOperations.snippet.model.dto

import jakarta.validation.constraints.NotBlank

data class CreateSnippetDTO(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "Content is required")
    val content: String,
    @field:NotBlank(message = "Language is required")
    val language: String,
)
