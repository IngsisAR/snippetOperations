package austral.ingsisAR.snippetOperations.redis.event

data class LintRequestEvent(
    val userId: String,
    val snippetId: String,
    val linterRules: LinterRulesDTO,
)