package austral.ingsisAHRE.snippetOperations.snippet.model.dto

import austral.ingsisAHRE.snippetOperations.snippet.model.enum.SnippetStatus

data class GetSnippetWithStatusDTO(
    val id: String,
    val name: String,
    val content: String,
    val language: String,
    val author: String,
    val status: SnippetStatus,
)
