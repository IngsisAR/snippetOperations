package austral.ingsisAHRE.snippetOperations.rule.repository

import austral.ingsisAHRE.snippetOperations.rule.model.entity.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, String> {
    fun findByName(name: String): Rule?
}
