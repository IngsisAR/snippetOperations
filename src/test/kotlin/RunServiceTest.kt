import austral.ingsisAR.snippetOperations.integration.RunnerService
import austral.ingsisAR.snippetOperations.rule.model.dto.GetUserRuleDTO
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.model.enum.ValueType
import austral.ingsisAR.snippetOperations.rule.service.RuleService
import austral.ingsisAR.snippetOperations.run.model.dto.SnippetContent
import austral.ingsisAR.snippetOperations.run.service.RunService
import austral.ingsisAR.snippetOperations.shared.exception.ConflictException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RunServiceTest {
    private lateinit var runService: RunService
    private lateinit var ruleService: RuleService
    private lateinit var runnerService: RunnerService

    @BeforeEach
    fun setUp() {
        ruleService = mock(RuleService::class.java)
        runnerService = mock(RunnerService::class.java)
        runService = RunService(ruleService, runnerService)
    }

    @Test
    fun `formatSnippet should return formatted snippet`() {
        val snippet = SnippetContent(content = "original content", language = "kotlin")
        val userId = "testUser"
        val token = "testToken"
        val rules =
            listOf(
                GetUserRuleDTO(
                    id = "1",
                    userId = userId,
                    name = "ruleName",
                    isActive = true,
                    value = "value",
                    valueType = ValueType.STRING,
                    ruleType = RuleType.FORMATTING,
                ),
            )
        val formattedContent = "formatted content"
        val responseEntity = ResponseEntity.ok(formattedContent)

        `when`(ruleService.getUserRulesByType(userId, RuleType.FORMATTING)).thenReturn(rules)
        `when`(runnerService.formatSnippet(snippet.content, snippet.language, ruleService.parseFormattingRules(rules), token)).thenReturn(
            responseEntity,
        )

        val result = runService.formatSnippet(snippet, userId, token)

        assertEquals(formattedContent, result)
    }

    @Test
    fun `formatSnippet should throw ConflictException when formatting fails`() {
        val snippet = SnippetContent(content = "original content", language = "kotlin")
        val userId = "testUser"
        val token = "testToken"
        val rules =
            listOf(
                GetUserRuleDTO(
                    id = "1",
                    userId = userId,
                    name = "ruleName",
                    isActive = true,
                    value = "value",
                    valueType = ValueType.STRING,
                    ruleType = RuleType.FORMATTING,
                ),
            )
        val responseEntity = ResponseEntity.status(409).body<String>(null)

        `when`(ruleService.getUserRulesByType(userId, RuleType.FORMATTING)).thenReturn(rules)
        `when`(runnerService.formatSnippet(snippet.content, snippet.language, ruleService.parseFormattingRules(rules), token)).thenReturn(
            responseEntity,
        )

        assertFailsWith<ConflictException> {
            runService.formatSnippet(snippet, userId, token)
        }
    }
}
