package austral.ingsisAR.snippetOperations.snippet.repository

import austral.ingsisAR.snippetOperations.snippet.model.entity.Snippet
import org.springframework.data.jpa.repository.JpaRepository

interface SnippetRepository : JpaRepository<Snippet, String>
