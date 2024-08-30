package austral.ingsisAHRE.snippetOperations.testCase.repository

import austral.ingsisAHRE.snippetOperations.testCase.model.entity.TestCase
import org.springframework.data.jpa.repository.JpaRepository

interface TestCaseRepository : JpaRepository<TestCase, String> {
    fun findAllBySnippetId(snippetId: String): List<TestCase>
}
