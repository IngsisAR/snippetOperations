package austral.ingsisAHRE.snippetOperations.snippet.model.dto

data class GetPaginatedSnippetWithStatusDTO(
    val snippets: List<GetSnippetWithStatusDTO>,
    val total: Int,
)
