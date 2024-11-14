package austral.ingsisAR.snippetOperations.snippet.repository

import austral.ingsisAR.snippetOperations.snippet.model.entity.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetRepository : JpaRepository<Snippet, String>
