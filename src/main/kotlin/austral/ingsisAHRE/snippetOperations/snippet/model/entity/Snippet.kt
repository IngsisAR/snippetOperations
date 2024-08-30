package austral.ingsisAHRE.snippetOperations.snippet.model.entity

import austral.ingsisAHRE.snippetOperations.shared.baseModel.BaseModel
import austral.ingsisAHRE.snippetOperations.testCase.model.entity.TestCase
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany

@Entity
data class Snippet(
    val name: String,
    val language: String,
    @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "snippet")
    val userSnippets: List<UserSnippet> = listOf(),
    @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "snippet")
    val tests: List<TestCase> = listOf(),
) : BaseModel()
