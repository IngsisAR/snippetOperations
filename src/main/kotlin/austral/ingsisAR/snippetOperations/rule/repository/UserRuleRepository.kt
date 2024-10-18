package austral.ingsisAR.snippetOperations.rule.repository

import austral.ingsisAR.snippetOperations.rule.model.entity.UserRule
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRuleRepository : JpaRepository<UserRule, String> {
    @Query("select (count(u) > 0) from UserRule u where u.userId = ?1")
    fun existsByUserId(userId: String): Boolean

    fun findAllByUserIdAndRuleRuleType(
        userId: String,
        ruleType: RuleType,
    ): List<UserRule>
}
