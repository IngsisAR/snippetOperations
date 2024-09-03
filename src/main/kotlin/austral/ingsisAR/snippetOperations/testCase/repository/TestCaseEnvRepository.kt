package austral.ingsisAR.snippetOperations.testCase.repository

import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCaseEnv
import org.springframework.data.jpa.repository.JpaRepository

interface TestCaseEnvRepository : JpaRepository<TestCaseEnv, String>
