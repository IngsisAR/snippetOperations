package austral.ingsisAR.snippetOperations.snippet.repository

import austral.ingsisAR.snippetOperations.snippet.model.entity.UserSnippet
import org.springframework.data.jpa.repository.JpaRepository

interface UserSnippetRepository : JpaRepository<UserSnippet, String> {
    fun findFirstBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): UserSnippet?

    fun findAllByUserId(userId: String): List<UserSnippet>
}
