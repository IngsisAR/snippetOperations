package austral.ingsisAR.snippetOperations.testCase.repository

import austral.ingsisAR.snippetOperations.testCase.model.entity.TestCase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestCaseRepository : JpaRepository<TestCase, String> {
    fun findAllBySnippetId(snippetId: String): List<TestCase>
}
