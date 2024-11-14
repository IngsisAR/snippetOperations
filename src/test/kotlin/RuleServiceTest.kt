import austral.ingsisAR.snippetOperations.redis.producer.LintRequestProducer
import austral.ingsisAR.snippetOperations.rule.model.dto.GetUserRuleDTO
import austral.ingsisAR.snippetOperations.rule.model.dto.UpdateUserRuleDTO
import austral.ingsisAR.snippetOperations.rule.model.entity.Rule
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.model.enum.ValueType
import austral.ingsisAR.snippetOperations.rule.repository.RuleRepository
import austral.ingsisAR.snippetOperations.rule.repository.UserRuleRepository
import austral.ingsisAR.snippetOperations.rule.service.RuleService
import austral.ingsisAR.snippetOperations.shared.exception.NotFoundException
import austral.ingsisAR.snippetOperations.snippet.service.SnippetService
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyList
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RuleServiceTest {
    private lateinit var ruleService: RuleService
    private lateinit var ruleRepository: RuleRepository
    private lateinit var userRuleRepository: UserRuleRepository
    private lateinit var snippetService: SnippetService
    private lateinit var lintRequestProducer: LintRequestProducer
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        ruleRepository = mock(RuleRepository::class.java)
        userRuleRepository = mock(UserRuleRepository::class.java)
        snippetService = mock(SnippetService::class.java)
        lintRequestProducer = mock(LintRequestProducer::class.java)
        objectMapper = mock(ObjectMapper::class.java)
        ruleService = RuleService(ruleRepository, userRuleRepository, snippetService, lintRequestProducer)
    }

    @Test
    fun `createUserDefaultRules should create default rules for user`() {
        val userId = "testUser"
        `when`(userRuleRepository.existsByUserId(userId)).thenReturn(false)
        `when`(ruleRepository.findAll()).thenReturn(
            listOf(
                Rule("rule1", "true", ValueType.BOOLEAN, RuleType.LINTING),
                Rule("rule2", "false", ValueType.BOOLEAN, RuleType.FORMATTING),
            ),
        )

        ruleService.createUserDefaultRules(userId)

        verify(userRuleRepository, times(1)).saveAll(anyList())
    }

    @Test
    fun `parseFormattingRules should parse formatting rules correctly`() {
        val rules =
            listOf(
                GetUserRuleDTO("1", "user1", "spaceBeforeColon", true, "1", ValueType.INTEGER, RuleType.FORMATTING),
                GetUserRuleDTO("2", "user1", "spaceAfterColon", true, "2", ValueType.INTEGER, RuleType.FORMATTING),
            )

        val result = ruleService.parseFormattingRules(rules)

        assertEquals(1, result.spaceBeforeColon)
        assertEquals(2, result.spaceAfterColon)
    }

    @Test
    fun `parseFormattingRules should parse identationInsideConditionals correctly`() {
        val rules =
            listOf(
                GetUserRuleDTO("1", "user1", "identationInsideConditionals", true, "4", ValueType.INTEGER, RuleType.FORMATTING),
            )

        val result = ruleService.parseFormattingRules(rules)

        assertEquals(4, result.identationInsideConditionals)
    }

    @Test
    fun `parseFormattingRules should parse spacesInAssignSymbol correctly`() {
        val rules =
            listOf(
                GetUserRuleDTO("1", "user1", "spacesInAssignSymbol", true, "3", ValueType.INTEGER, RuleType.FORMATTING),
            )

        val result = ruleService.parseFormattingRules(rules)

        assertEquals(3, result.spacesInAssignSymbol)
    }

    @Test
    fun `parseFormattingRules should parse lineJumpBeforePrintln correctly`() {
        val rules =
            listOf(
                GetUserRuleDTO("1", "user1", "lineJumpBeforePrintln", true, "2", ValueType.INTEGER, RuleType.FORMATTING),
            )

        val result = ruleService.parseFormattingRules(rules)

        assertEquals(2, result.lineJumpBeforePrintln)
    }

    @Test
    fun `updateUserRules should throw NotFoundException if rule not found`(): Unit =
        runBlocking {
            val userId = "testUser"
            val userRules =
                listOf(
                    UpdateUserRuleDTO(userId, "true", true),
                )

            `when`(userRuleRepository.findById(userId)).thenReturn(Optional.empty())

            assertFailsWith<NotFoundException> {
                ruleService.updateUserRules(userId, userRules)
            }
        }
}
