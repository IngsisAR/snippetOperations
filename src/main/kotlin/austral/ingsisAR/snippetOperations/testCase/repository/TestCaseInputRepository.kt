package austral.ingsisAR.snippetOperations.testCase.repository

import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseInput
import org.springframework.data.jpa.repository.JpaRepository

interface TestCaseInputRepository : JpaRepository<TestCaseInput, String>