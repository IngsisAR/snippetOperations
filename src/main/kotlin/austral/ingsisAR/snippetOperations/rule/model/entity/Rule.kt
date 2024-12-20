package austral.ingsisAR.snippetOperations.rule.model.entity

import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.model.enum.ValueType
import austral.ingsisAR.snippetOperations.shared.baseModel.BaseModel
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
data class Rule(
    val name: String,
    val defaultValue: String,
    @Enumerated(EnumType.STRING)
    val valueType: ValueType,
    @Enumerated(EnumType.STRING)
    val ruleType: RuleType,
) : BaseModel()
