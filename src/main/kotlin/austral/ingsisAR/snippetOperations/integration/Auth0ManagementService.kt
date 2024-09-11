package austral.ingsisAR.snippetOperations.integration

import austral.ingsisAR.snippetOperations.shared.exception.BadRequestException
import austral.ingsisAR.snippetOperations.shared.exception.NotFoundException
import austral.ingsisAR.snippetOperations.shared.exception.UnauthorizedException
import austral.ingsisAR.snippetOperations.user.model.dto.UserDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.io.File
import java.util.Base64

@Component
class Auth0ManagementService
    @Autowired
    constructor(
        private val rest: RestTemplate,
        @Value("\${auth0.issuer.uri}")
        private val auth0Url: String,
        @Value("\${auth0.management.token}")
        private var managementToken: String,
        @Value("\${auth0.management.client.id}")
        private val clientId: String,
        @Value("\${auth0.management.client.secret}")
        private val clientSecret: String,
        @Value("\${auth0.management.audience}")
        private val audience: String,
    ) {
        fun getUsers(
            page: Int,
            perPage: Int,
            name: String,
        ): ResponseEntity<List<UserDTO>> {
            try {
                val request: HttpEntity<Void> = HttpEntity(getHeaders())
                return rest.exchange<List<UserDTO>>(
                    "{$audience}users?fields=user_id,nickname&per_page=5&page=0&q=nickname:*$name*",
                    HttpMethod.GET,
                    request,
                )
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        fun getUserById(userId: String): ResponseEntity<UserDTO> {
            try {
                val request: HttpEntity<Void> = HttpEntity(getHeaders())
                return rest.exchange("${audience}users/$userId", HttpMethod.GET, request, UserDTO::class.java)
            } catch (e: Exception) {
                throw BadRequestException("Failed to get user: ${e.message}")
            }
        }

        fun requestNewManagementToken(): String {
            val headers =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                    set("User-Agent", "IntelliJ HTTP Client/IntelliJ IDEA 2024.1.6")
                    set("Accept", "*/*")
                }

            val body =
                TokenRequest(
                    client_id = clientId,
                    client_secret = clientSecret,
                    audience = audience,
                    grant_type = "client_credentials",
                )
            val request = HttpEntity(body, headers)

            val response =
                try {
                    rest.exchange(
                        "${auth0Url}oauth/token",
                        HttpMethod.POST,
                        request,
                        object : ParameterizedTypeReference<Map<String, String>>() {},
                    )
                } catch (e: HttpStatusCodeException) {
                    throw UnauthorizedException("Failed to get new management token: ${e.statusCode} ${e.responseBodyAsString}")
                } catch (e: RestClientException) {
                    throw UnauthorizedException("Failed to get new management token: ${e.message}")
                }

            return response.body?.get("access_token") ?: throw NotFoundException("No access token in response")
        }

        private fun getHeaders(): HttpHeaders {
            if (isTokenExpired(managementToken)) {
                managementToken = requestNewManagementToken()
                updateEnvironmentVariable("AUTH0_MANAGEMENT_TOKEN", managementToken)
            }
            return HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $managementToken")
            }
        }

        private fun isTokenExpired(token: String): Boolean {
            val parts = token.split(".")
            if (parts.size != 3) return true

            val payload = String(Base64.getDecoder().decode(parts[1]))
            val mapper = jacksonObjectMapper()
            val jsonObject: Map<String, Any> = mapper.readValue(payload)
            val exp = jsonObject["exp"] as Int
            val now = System.currentTimeMillis() / 1000

            return now >= exp
        }

        private fun updateEnvironmentVariable(
            key: String,
            value: String,
        ) {
            val envFile = File("env/operations.env")
            val lines = envFile.readLines().toMutableList()
            val index = lines.indexOfFirst { it.startsWith("$key=") }
            if (index != -1) {
                lines[index] = "$key=$value"
            } else {
                lines.add("$key=$value")
            }
            envFile.writeText(lines.joinToString("\n"))
        }
    }

data class TokenRequest(
    val client_id: String,
    val client_secret: String,
    val audience: String,
    val grant_type: String,
)
