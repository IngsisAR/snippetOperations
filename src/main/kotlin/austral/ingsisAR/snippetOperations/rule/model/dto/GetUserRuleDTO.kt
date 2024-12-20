package austral.ingsisAR.snippetOperations.rule.model.dto

import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.model.enum.ValueType

data class GetUserRuleDTO(
    val id: String,
    val userId: String,
    val name: String,
    val isActive: Boolean,
    val value: String,
    val valueType: ValueType,
    val ruleType: RuleType,
)
