package austral.ingsisAR.snippetOperations.redis.producer

import austral.ingsisAR.snippetOperations.redis.event.LintRequestEvent
import austral.ingsisAR.snippetOperations.redis.event.LinterRulesDTO
import austral.ingsisAR.snippetOperations.rule.model.entity.UserRule
import austral.ingsisAR.snippetOperations.rule.model.enum.RuleType
import austral.ingsisAR.snippetOperations.rule.repository.UserRuleRepository
import austral.ingsisAR.snippetOperations.snippet.model.entity.Snippet
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.austral.ingsis.redis.RedisStreamProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class LintRequestProducer
    @Autowired
    constructor(
        @Value("\${redis.streams.lintRequest}")
        streamName: String,
        redis: RedisTemplate<String, String>,
        private val objectMapper: ObjectMapper,
        private val userRuleRepository: UserRuleRepository,
    ) : RedisStreamProducer(streamName, redis) {
        private val logger: Logger = LoggerFactory.getLogger(LintRequestProducer::class.java)
        private val publishRetry: Int = 3

        suspend fun publishLintEvents(
            userId: String,
            snippets: List<Snippet>,
        ) {
            coroutineScope {
                snippets.forEach { snippet ->
                    launch {
                        repeat(publishRetry) {
                            try {
                                val event =
                                    objectMapper.writeValueAsString(
                                        LintRequestEvent(
                                            userId = userId,
                                            snippetId = snippet.id!!,
                                            language = snippet.language,
                                            linterRules = parseLintingRules(getLintingRules(userId)),
                                        ),
                                    )
                                logger.info("Publishing lint request event:\n$event")
                                emit(event)
                                return@launch // Salir una vez que la publicación es exitosa
                            } catch (e: Exception) {
                                logger.error(
                                    "Error publishing lint request event for " +
                                        "Snippet(${snippet.name}|${snippet.id}) for User($userId)",
                                )
                            }
                        }
                    }
                }
            }
        }

        private fun getLintingRules(userId: String): List<UserRule> {
            return userRuleRepository.findAllByUserIdAndRuleRuleType(userId, RuleType.LINTING)
        }

        private fun parseLintingRules(rules: List<UserRule>): LinterRulesDTO {
            val result = LinterRulesDTO()

            rules.forEach {
                when (it.rule.name) {
                    "printlnNoExpressionArguments" -> result.printlnNoExpressionArguments = it.value.toBoolean()
                    "identifierCasing" -> result.identifierCasing = it.value
                    "readInputNoExpressionArguments" -> result.readInputNoExpressionArguments = it.value.toBoolean()
                }
            }
            return result
        }
    }
