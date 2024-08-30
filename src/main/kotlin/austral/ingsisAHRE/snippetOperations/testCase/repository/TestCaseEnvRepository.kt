package austral.ingsisAHRE.snippetOperations.testCase.repository

import austral.ingsisAHRE.snippetOperations.testCase.model.entity.TestCaseEnv
import org.springframework.data.jpa.repository.JpaRepository

interface TestCaseEnvRepository : JpaRepository<TestCaseEnv, String>
