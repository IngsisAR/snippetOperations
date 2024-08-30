package austral.ingsisAHRE.snippetOperations.testCase.repository

import austral.ingsisAHRE.snippetOperations.testCase.model.entity.TestCaseExpectedOutput
import org.springframework.data.jpa.repository.JpaRepository

interface TestCaseExpectedOutputRepository : JpaRepository<TestCaseExpectedOutput, String> {
    fun findAllByTestCaseId(testCaseId: String): List<TestCaseExpectedOutput>
}
