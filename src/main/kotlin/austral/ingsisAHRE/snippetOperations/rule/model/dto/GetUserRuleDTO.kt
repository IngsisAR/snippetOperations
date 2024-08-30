package austral.ingsisAHRE.snippetOperations.rule.model.dto

import austral.ingsisAHRE.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAHRE.snippetOperations.rule.model.enum.ValueType

data class GetUserRuleDTO(
    val id: String,
    val userId: String,
    val name: String,
    val active: Boolean,
    val value: String,
    val valueType: ValueType,
    val ruleType: RuleType,
)
