package austral.ingsisAR.snippetOperations.redis.consumer

import austral.ingsisAR.snippetOperations.redis.event.LintResultEvent
import austral.ingsisAR.snippetOperations.redis.event.LintStatus
import austral.ingsisAR.snippetOperations.snippet.model.enum.SnippetStatus
import austral.ingsisAR.snippetOperations.snippet.service.SnippetService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
        val groupName: String,
        redis: RedisTemplate<String, String>,
        private val snippetService: SnippetService,
        private val objectMapper: ObjectMapper,
    ) : RedisStreamConsumer<String>(streamName, groupName, redis), CoroutineScope {
        private val logger: Logger = LoggerFactory.getLogger(LintResultConsumer::class.java)

        // Define a coroutine scope tied to the lifecycle of the component
        private val job = Job()
        override val coroutineContext = Dispatchers.IO + job

        // Ensure coroutines are properly cancelled when the component is destroyed
        @PreDestroy
        fun cleanup() {
            job.cancel()
        }

        override fun onMessage(record: ObjectRecord<String, String>) {
            launch {
                try {
                    val lintResult: LintResultEvent = objectMapper.readValue(record.value)
                    logger.info("Consuming lint result for Snippet(${lintResult.snippetId}) for User(${lintResult.userId})")

                    snippetService.updateUserSnippetStatusBySnippetId(
                        lintResult.userId,
                        lintResult.snippetId,
                        parseLintStatus(lintResult.status),
                    )
                } catch (ex: Exception) {
                    logger.error("Error processing lint result: ${ex.message}")
                }
            }
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
