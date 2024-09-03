package austral.ingsisAR.snippetOperations.integration

import austral.ingsisAR.snippetOperations.shared.log.CorrelationIdFilter.Companion.CORRELATION_ID_KEY
import austral.ingsisAR.snippetOperations.testCase.model.dto.TestCaseEnvDTO
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RunnerService
    @Autowired
    constructor(
        private val rest: RestTemplate,
        @Value("\${runner.url}")
        private val runnerUrl: String,
    ) {
        fun runSnippet(
            content: String,
            inputs: List<String>,
            envs: List<TestCaseEnvDTO>,
            token: String,
        ): ResponseEntity<RunOutputDTO> {
            try {
                val request = HttpEntity(CreateRunDTO(content, inputs, envs), getHeaders(token))
                return rest.postForEntity("$runnerUrl/run", request, RunOutputDTO::class.java)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body(RunOutputDTO(listOf(), listOf(e.message ?: "An error occurred")))
            }
        }

        fun formatSnippet(
            content: String,
            formatterRules: FormatterRulesDTO,
            token: String,
        ): ResponseEntity<String> {
            try {
                val request =
                    HttpEntity(
                        FormatSnippetRequestDTO(
                            content = content,
                            formatterRules = formatterRules,
                        ),
                        getHeaders(token),
                    )
                return rest.postForEntity("$runnerUrl/format", request, String::class.java)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        private fun getHeaders(token: String): HttpHeaders {
            return HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $token")
                set("X-Correlation-Id", MDC.get(CORRELATION_ID_KEY))
            }
        }
    }
