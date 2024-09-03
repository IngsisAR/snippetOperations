package austral.ingsisAR.snippetOperations.redis.event

data class LintResultEvent(
    val userId: String,
    val snippetId: String,
    val status: LintStatus,
)
