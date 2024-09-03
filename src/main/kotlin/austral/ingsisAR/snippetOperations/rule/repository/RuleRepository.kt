package austral.ingsisAR.snippetOperations.rule.repository

import austral.ingsisAR.snippetOperations.rule.model.entity.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, String> {
    fun findByName(name: String): Rule?
}
