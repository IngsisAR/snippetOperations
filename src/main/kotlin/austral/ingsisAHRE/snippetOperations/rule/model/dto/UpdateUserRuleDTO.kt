package austral.ingsisAHRE.snippetOperations.rule.model.dto

import jakarta.validation.constraints.NotBlank

data class UpdateUserRuleDTO(
    @field:NotBlank(message = "id is required")
    val id: String,
    @field:NotBlank(message = "value is required")
    val value: String,
    val active: Boolean,
)
