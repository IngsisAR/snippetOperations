package austral.ingsisAHRE.snippetOperations.testCase.model.entity

import austral.ingsisAHRE.snippetOperations.shared.baseModel.BaseModel
import austral.ingsisAHRE.snippetOperations.snippet.model.entity.Snippet
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
data class TestCase(
    var name: String,
    @ManyToOne
    @JoinColumn(name = "snippet_id", nullable = false)
    val snippet: Snippet,
    @OneToMany(mappedBy = "testCase", cascade = [CascadeType.REMOVE])
    val inputs: List<TestCaseInput> = listOf(),
    @OneToMany(mappedBy = "testCase", cascade = [CascadeType.REMOVE])
    val expectedOutputs: List<TestCaseExpectedOutput> = listOf(),
    @OneToMany(mappedBy = "testCase", cascade = [CascadeType.REMOVE])
    val envs: List<TestCaseEnv> = listOf(),
) : BaseModel()
