package austral.ingsisAR.snippetOperations.testCase.model.entity

import austral.ingsisAR.snippetOperations.shared.baseModel.BaseModel
import austral.ingsisAR.snippetOperations.snippet.model.entity.Snippet
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
data class TestCase(
    var name: String,
    @ManyToOne
    @JoinColumn(name = "snippet_id", nullable = false)
    val snippet: Snippet,
    @OneToMany(mappedBy = "testCase", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
    val inputs: List<TestCaseInput> = listOf(),
    @OneToMany(mappedBy = "testCase", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
    val expectedOutputs: List<TestCaseExpectedOutput> = listOf(),
    @OneToMany(mappedBy = "testCase", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER)
    val envs: List<TestCaseEnv> = listOf(),
) : BaseModel()
