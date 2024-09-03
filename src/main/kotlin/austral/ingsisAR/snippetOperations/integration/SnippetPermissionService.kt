package austral.ingsisAR.snippetOperations.integration

import austral.ingsisAR.snippetOperations.shared.log.CorrelationIdFilter.Companion.CORRELATION_ID_KEY
import austral.ingsisAR.snippetOperations.snippet.model.dto.CreateSnippetPermissionDTO
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class SnippetPermissionService
    @Autowired
    constructor(
        private val rest: RestTemplate,
        @Value("\${snippet-permission.url}")
        private val snippetUrl: String,
    ) {
        fun createSnippetPermission(
            permission: CreateSnippetPermissionDTO,
            token: String,
        ): ResponseEntity<Void> {
            try {
                val request = HttpEntity(permission, getHeaders(token))
                return rest.postForEntity(snippetUrl, request, Void::class.java)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        fun getAuthorBySnippetId(
            snippetId: String,
            token: String,
        ): ResponseEntity<String> {
            try {
                val request: HttpEntity<Void> = HttpEntity(getHeaders(token))
                return rest.exchange("$snippetUrl/author/$snippetId", HttpMethod.GET, request, String::class.java)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        fun getAllSnippetPermissions(
            userId: String,
            token: String,
            pageNumber: Int,
            pageSize: Int,
        ): ResponseEntity<GetPaginatedSnippetPermissionsDTO> {
            try {
                val request: HttpEntity<Void> = HttpEntity(getHeaders(token))
                return rest.exchange(
                    "$snippetUrl/all?page_number=$pageNumber&page_size=$pageSize",
                    HttpMethod.GET,
                    request,
                    GetPaginatedSnippetPermissionsDTO::class.java,
                )
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        fun deleteSnippetPermissions(
            snippetId: String,
            token: String,
        ): ResponseEntity<Void> {
            try {
                val request: HttpEntity<Void> = HttpEntity(getHeaders(token))
                return rest.exchange("$snippetUrl/all/$snippetId", HttpMethod.DELETE, request, Void::class.java)
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
