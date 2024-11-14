import austral.ingsisAR.snippetOperations.rule.controller.RuleController
import austral.ingsisAR.snippetOperations.rule.model.dto.GetUserRuleDTO
import austral.ingsisAR.snippetOperations.rule.model.dto.UpdateUserRuleDTO
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.model.enum.ValueType
import austral.ingsisAR.snippetOperations.rule.service.RuleService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals

class RuleControllerTest {
    private lateinit var ruleController: RuleController
    private lateinit var ruleService: RuleService
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        ruleService = mock(RuleService::class.java)
        ruleController = RuleController(ruleService)
        mockMvc = MockMvcBuilders.standaloneSetup(ruleController).build()
    }

    @Test
    fun `createUserDefaultRules should return OK`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")

        val response: ResponseEntity<Void> = ruleController.createUserDefaultRules(jwt)

        assertEquals(ResponseEntity.ok().build(), response)
        verify(ruleService).createUserDefaultRules("testUser")
    }

    @Test
    fun `getRules should return list of GetUserRuleDTO`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")
        val ruleType = RuleType.FORMATTING
        val userRules =
            listOf(
                GetUserRuleDTO(
                    id = "1",
                    userId = "testUser",
                    name = "ruleName",
                    isActive = true,
                    value = "value",
                    valueType = ValueType.STRING,
                    ruleType = ruleType,
                ),
            )
        `when`(ruleService.getUserRulesByType("testUser", ruleType)).thenReturn(userRules)

        val response: ResponseEntity<List<GetUserRuleDTO>> = ruleController.getRules(ruleType, jwt)

        assertEquals(ResponseEntity.ok(userRules), response)
        verify(ruleService).getUserRulesByType("testUser", ruleType)
    }

    @Test
    fun `updateUserRules should return updated list of GetUserRuleDTO`(): Unit =
        runBlocking {
            val jwt = mock(Jwt::class.java)
            `when`(jwt.subject).thenReturn("testUser")
            val userRules =
                listOf(
                    UpdateUserRuleDTO(
                        id = "1",
                        value = "newValue",
                        active = true,
                    ),
                )
            val updatedUserRules =
                listOf(
                    GetUserRuleDTO(
                        id = "1",
                        userId = "testUser",
                        name = "ruleName",
                        isActive = true,
                        value = "newValue",
                        valueType = ValueType.STRING,
                        ruleType = RuleType.FORMATTING,
                    ),
                )
            `when`(ruleService.updateUserRules("testUser", userRules)).thenReturn(updatedUserRules)

            val response: ResponseEntity<List<GetUserRuleDTO>> = ruleController.updateUserRules(userRules, jwt)

            assertEquals(ResponseEntity.ok(updatedUserRules), response)
            verify(ruleService).updateUserRules("testUser", userRules)
        }
}
