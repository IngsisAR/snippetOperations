package austral.ingsisAR.snippetOperations.integration

import austral.ingsisAR.snippetOperations.user.model.dto.UserDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Component
class Auth0Service
    @Autowired
    constructor(
        private val rest: RestTemplate,
        @Value("\${auth0.issuer.uri}")
        private val auth0Url: String,
        @Value("\${auth0.management.token}")
        private val token: String,
    ) {
        fun getUsers(
            page: Int,
            perPage: Int,
            name: String,
        ): ResponseEntity<List<UserDTO>> {
            try {
                val request: HttpEntity<Void> = HttpEntity(getHeaders())
                return rest.exchange<List<UserDTO>>(
                    "$auth0Url/api/v2/users?fields=user_id,nickname&per_page=5&page=0&q=nickname:*$name*",
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
                return rest.exchange("$auth0Url/api/v2/users/$userId", HttpMethod.GET, request, UserDTO::class.java)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        private fun getHeaders(): HttpHeaders {
            return HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $token")
            }
        }
    }
