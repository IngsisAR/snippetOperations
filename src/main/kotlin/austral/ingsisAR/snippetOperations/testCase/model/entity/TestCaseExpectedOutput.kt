package austral.ingsisAR.snippetOperations.testCase.model.entity

import austral.ingsisAR.snippetOperations.shared.baseModel.BaseModel
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class TestCaseExpectedOutput(
    val output: String,
    @ManyToOne
    @JoinColumn(name = "test_case_id", nullable = false)
    val testCase: TestCase,
) : BaseModel()
