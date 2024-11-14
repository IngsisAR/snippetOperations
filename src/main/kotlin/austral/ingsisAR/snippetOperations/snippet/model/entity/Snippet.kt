package austral.ingsisAR.snippetOperations.snippet.model.entity

import austral.ingsisAR.snippetOperations.shared.baseModel.BaseModel
import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCase
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany

@Entity
data class Snippet(
    val name: String,
    val language: String,
    @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "snippet", fetch = FetchType.EAGER)
    val userSnippets: List<UserSnippet> = listOf(),
    @OneToMany(cascade = [CascadeType.REMOVE], mappedBy = "snippet")
    val tests: List<TestCase> = listOf(),
) : BaseModel()
