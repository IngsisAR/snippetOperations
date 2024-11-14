package austral.ingsisAR.snippetOperations.snippet.model.dto

data class GetSnippetDTO(
    val id: String,
    val name: String,
    val content: String,
    val language: String,
    val author: String,
)
