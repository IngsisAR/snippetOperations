package austral.ingsisAR.snippetOperations.rule.model.entity

import austral.ingsisAR.snippetOperations.shared.baseModel.BaseModel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "rule_id"])],
)
data class UserRule(
    val userId: String,
    @Column(name = "rule_value")
    var value: String,
    var active: Boolean,
    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false)
    val rule: Rule,
) : BaseModel()
