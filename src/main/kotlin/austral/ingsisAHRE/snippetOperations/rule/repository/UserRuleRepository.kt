package austral.ingsisAHRE.snippetOperations.rule.repository

import austral.ingsisAHRE.snippetOperations.rule.model.entity.UserRule
import austral.ingsisAHRE.snippetOperations.rule.model.enum.RuleType
import org.springframework.data.jpa.repository.JpaRepository

interface UserRuleRepository : JpaRepository<UserRule, String> {
    fun findAllByUserIdAndRuleRuleType(
        userId: String,
        ruleType: RuleType,
    ): List<UserRule>
}
