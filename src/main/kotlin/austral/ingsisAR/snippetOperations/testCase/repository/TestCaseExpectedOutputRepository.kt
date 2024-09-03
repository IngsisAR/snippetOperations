package austral.ingsisAR.snippetOperations.testCase.repository

import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseExpectedOutput
import org.springframework.data.jpa.repository.JpaRepository

interface TestCaseExpectedOutputRepository : JpaRepository<TestCaseExpectedOutput, String>
