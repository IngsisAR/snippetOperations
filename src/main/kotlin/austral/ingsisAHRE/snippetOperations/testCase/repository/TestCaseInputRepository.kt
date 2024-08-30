package austral.ingsisAHRE.snippetOperations.testCase.repository

import austral.ingsisAHRE.snippetOperations.testCase.model.entity.TestCaseInput
import org.springframework.data.jpa.repository.JpaRepository

interface TestCaseInputRepository : JpaRepository<TestCaseInput, String>
