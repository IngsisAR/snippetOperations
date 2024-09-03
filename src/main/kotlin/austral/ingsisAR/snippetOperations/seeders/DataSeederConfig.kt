package austral.ingsisAR.snippetOperations.seeders

import austral.ingsisAR.snippetOperations.rule.model.entity.Rule
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.model.enum.ValueType
import austral.ingsisAR.snippetOperations.rule.repository.RuleRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataSeederConfig {
    @Bean
    fun dataSeeder(ruleRepository: RuleRepository): CommandLineRunner {
        return CommandLineRunner {
            // Seed data
            val rules =
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

            rules.forEach { rule ->
                ruleRepository.findByName(rule.name) ?: ruleRepository.save(rule)
            }
        }
    }
}
