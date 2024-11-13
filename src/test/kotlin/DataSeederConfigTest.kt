import austral.ingsisAR.snippetOperations.rule.model.entity.Rule
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.model.enum.ValueType
import austral.ingsisAR.snippetOperations.rule.repository.RuleRepository
import austral.ingsisAR.snippetOperations.seeders.DataSeederConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class DataSeederConfigTest {
    private lateinit var ruleRepository: RuleRepository
    private lateinit var dataSeederConfig: DataSeederConfig

    @BeforeEach
    fun setUp() {
        ruleRepository = mock(RuleRepository::class.java)
        dataSeederConfig = DataSeederConfig()
    }

    @Test
    fun `dataSeeder should seed rules`() {
        val commandLineRunner = dataSeederConfig.dataSeeder(ruleRepository)
        `when`(ruleRepository.findByName(anyString())).thenReturn(null)
        `when`(ruleRepository.save(any(Rule::class.java))).thenAnswer { it.arguments[0] }

        commandLineRunner.run()

        val expectedRules =
            listOf(
                Rule(
                    name = "printlnNoExpressionArguments",
                    defaultValue = "true",
                    ruleType = RuleType.LINTING,
                    valueType = ValueType.BOOLEAN,
                ),
                Rule(
                    name = "identifierCasing",
                    defaultValue = "camel case",
                    ruleType = RuleType.LINTING,
                    valueType = ValueType.STRING,
                ),
                Rule(
                    name = "readInputNoExpressionArguments",
                    defaultValue = "true",
                    ruleType = RuleType.LINTING,
                    valueType = ValueType.BOOLEAN,
                ),
                Rule(
                    name = "spaceBeforeColon",
                    defaultValue = "1",
                    ruleType = RuleType.FORMATTING,
                    valueType = ValueType.INTEGER,
                ),
                Rule(
                    name = "spaceAfterColon",
                    defaultValue = "1",
                    ruleType = RuleType.FORMATTING,
                    valueType = ValueType.INTEGER,
                ),
                Rule(
                    name = "spacesInAssignSymbol",
                    defaultValue = "1",
                    ruleType = RuleType.FORMATTING,
                    valueType = ValueType.INTEGER,
                ),
                Rule(
                    name = "lineJumpBeforePrintln",
                    defaultValue = "1",
                    ruleType = RuleType.FORMATTING,
                    valueType = ValueType.INTEGER,
                ),
                Rule(
                    name = "identationInsideConditionals",
                    defaultValue = "1",
                    ruleType = RuleType.FORMATTING,
                    valueType = ValueType.INTEGER,
                ),
            )

        expectedRules.forEach { rule ->
            verify(ruleRepository, times(1)).findByName(rule.name)
            verify(ruleRepository, times(1)).save(rule)
        }
    }
}
