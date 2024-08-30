package austral.ingsisAHRE.snippetOperations.snippet.model.entity

import austral.ingsisAHRE.snippetOperations.shared.baseModel.BaseModel
import austral.ingsisAHRE.snippetOperations.snippet.model.enum.SnippetStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class UserSnippet(
    val userId: String,
    @Enumerated(EnumType.STRING)
    var status: SnippetStatus,
    @ManyToOne
    @JoinColumn(name = "snippet_id", nullable = false)
    val snippet: Snippet,
) : BaseModel()
