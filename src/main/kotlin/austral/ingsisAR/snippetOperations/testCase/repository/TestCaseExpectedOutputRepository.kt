package austral.ingsisAR.snippetOperations.testCase.repository

import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseExpectedOutput
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestCaseExpectedOutputRepository : JpaRepository<TestCaseExpectedOutput, String>
