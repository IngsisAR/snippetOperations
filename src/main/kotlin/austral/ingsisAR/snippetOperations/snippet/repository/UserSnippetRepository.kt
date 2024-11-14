package austral.ingsisAR.snippetOperations.snippet.repository

import austral.ingsisAR.snippetOperations.snippet.model.entity.UserSnippet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSnippetRepository : JpaRepository<UserSnippet, String> {
    fun findFirstBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): UserSnippet?

    fun findAllByUserId(userId: String): List<UserSnippet>
}
