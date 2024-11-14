package austral.ingsisAR.snippetOperations.snippet.model.dto

import austral.ingsisAR.snippetOperations.snippet.model.enum.SnippetStatus

data class GetSnippetWithStatusDTO(
    val id: String,
    val name: String,
    val content: String,
    val language: String,
    val author: String,
    val status: SnippetStatus,
)
