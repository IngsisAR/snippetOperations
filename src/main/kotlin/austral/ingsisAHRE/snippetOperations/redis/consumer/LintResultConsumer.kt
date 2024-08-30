package austral.ingsisAHRE.snippetOperations.redis.consumer

import austral.ingsisAHRE.snippetOperations.redis.event.LintResultEvent
import austral.ingsisAHRE.snippetOperations.redis.event.LintStatus
import austral.ingsisAHRE.snippetOperations.snippet.model.enum.SnippetStatus
import austral.ingsisAHRE.snippetOperations.snippet.service.SnippetService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.austral.ingsis.redis.RedisStreamConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component

@Component
class LintResultConsumer
    @Autowired
    constructor(
        @Value("\${redis.streams.lintResult}")
        streamName: String,
        @Value("\${redis.groups.lint}")
        groupName: String,
        redis: RedisTemplate<String, String>,
        private val snippetService: SnippetService,
        private val objectMapper: ObjectMapper,
    ) : RedisStreamConsumer<String>(streamName, groupName, redis) {
        init {
            subscription()
        }
        private val logger: Logger = LoggerFactory.getLogger(LintResultConsumer::class.java)

        override fun onMessage(record: ObjectRecord<String, String>) {
            val lintResult: LintResultEvent = objectMapper.readValue(record.value)
            logger.info("Consuming lint result for Snippet(${lintResult.snippetId}) for User(${lintResult.userId})")

            snippetService.updateUserSnippetStatusBySnippetId(
                lintResult.userId,
                lintResult.snippetId,
                parseLintStatus(lintResult.status),
            )
        }

        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(java.time.Duration.ofMillis(10000))
                .targetType(String::class.java)
                .build()
        }

        private fun parseLintStatus(status: LintStatus): SnippetStatus {
            return when (status) {
                LintStatus.PASSED -> SnippetStatus.COMPLIANT
                LintStatus.FAILED -> SnippetStatus.NOT_COMPLIANT
                LintStatus.PENDING -> SnippetStatus.PENDING
            }
        }
    }
