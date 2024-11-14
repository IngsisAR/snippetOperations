package austral.ingsisAR.snippetOperations.testCase.repository

import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseInput
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestCaseInputRepository : JpaRepository<TestCaseInput, String>
