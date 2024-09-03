package austral.ingsisAR.snippetOperations.integration

import austral.ingsisAR.snippetOperations.shared.log.CorrelationIdFilter.Companion.CORRELATION_ID_KEY
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Component
class AssetService
    @Autowired
    constructor(
        val rest: RestTemplate,
        @Value("\${bucket.url}")
        val bucketUrl: String,
    ) {
        fun saveSnippet(
            snippetId: String,
            content: String,
        ): ResponseEntity<String> {
            try {
                val request = HttpEntity(content, getHeaders())
                return rest.postForEntity("$bucketUrl/$snippetId", request, String::class.java)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        fun updateSnippet(
            snippetId: String,
            content: String,
        ): ResponseEntity<String> {
            try {
                return rest.postForEntity("$bucketUrl/$snippetId", content)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        fun getSnippet(snippetId: String): ResponseEntity<String> {
            try {
                return rest.getForEntity("$bucketUrl/$snippetId", String::class.java)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        fun deleteSnippet(snippetId: String): ResponseEntity<String> {
            try {
                rest.delete("$bucketUrl/$snippetId")
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
            return ResponseEntity.ok().build()
        }

        private fun getHeaders(): HttpHeaders {
            return HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("X-Correlation-Id", MDC.get(CORRELATION_ID_KEY))
            }
        }
    }
