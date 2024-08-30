package austral.ingsisAHRE.snippetOperations.snippet.repository

import austral.ingsisAHRE.snippetOperations.snippet.model.entity.Snippet
import org.springframework.data.jpa.repository.JpaRepository

interface SnippetRepository : JpaRepository<Snippet, String>
