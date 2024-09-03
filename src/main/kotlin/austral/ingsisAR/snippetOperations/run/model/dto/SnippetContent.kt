package austral.ingsisAR.snippetOperations.run.model.dto

import jakarta.validation.constraints.NotBlank

data class SnippetContent(
    @field:NotBlank(message = "Content can't be blank")
    val content: String,
)
