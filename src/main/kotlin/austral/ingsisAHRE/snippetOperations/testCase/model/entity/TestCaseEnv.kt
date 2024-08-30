package austral.ingsisAHRE.snippetOperations.testCase.model.entity

import austral.ingsisAHRE.snippetOperations.shared.baseModel.BaseModel
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class TestCaseEnv(
    val key: String,
    val value: String,
    @ManyToOne
    @JoinColumn(name = "test_case_id", nullable = false)
    val testCase: TestCase,
) : BaseModel()
